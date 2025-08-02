package com.davidrevolt.core.network.di

import android.content.Context
import coil3.ImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.davidrevolt.core.network.BuildConfig
import com.davidrevolt.core.network.FirebaseAiDataSource
import com.davidrevolt.core.network.FirebaseAiDataSourceImpl
import com.davidrevolt.core.network.SolarisNetworkDataSource
import com.davidrevolt.core.network.SolarisNetworkDataSourceImpl
import com.davidrevolt.core.network.model.NetworkLocation
import com.davidrevolt.core.network.model.NetworkLocationDeserializer
import com.davidrevolt.core.network.retrofit.RetrofitMeteosourceNetwork
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    // Binding
    @Binds
    @Singleton
    abstract fun bindsFirebaseAiDataSource(firebaseAiDataSourceImpl: FirebaseAiDataSourceImpl): FirebaseAiDataSource

    @Binds
    @Singleton
    abstract fun bindsSolarisNetworkDataSource(solarisNetworkDataSourceImpl: SolarisNetworkDataSourceImpl): SolarisNetworkDataSource

    companion object {
        // Custom OkHttp [retrofit and coil can use its own OkHttp if not mentioned]
        private const val TIMEOUT_SECONDS: Long = 120
        private const val BACKEND_URL = "https://www.meteosource.com/"

        @Provides
        @Singleton
        fun okHttpCallFactory(): Call.Factory {
            return OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .apply {
                            if (BuildConfig.DEBUG) {
                                setLevel(HttpLoggingInterceptor.Level.BODY)
                            }
                        },
                )
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofitMeteosourceNetwork(
            okHttpCallFactory: dagger.Lazy<Call.Factory> // delays the creation of okHttpCallFactory until it's actually used.
        ): RetrofitMeteosourceNetwork {
            val customGsonConverters = GsonBuilder()
                .registerTypeAdapter(NetworkLocation::class.java, NetworkLocationDeserializer())
                .create()
            return Retrofit.Builder()
                .callFactory { okHttpCallFactory.get().newCall(it) }
                .baseUrl(BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create(customGsonConverters)) // Gson Converters
                .build()
                .create(RetrofitMeteosourceNetwork::class.java) // The Interface
        }

        /**
         * Custom Coil ImageLoader Configuration with option to support Gif & Svg formats.
         * This configuration will use in all app modules
         * During Coil's initialization it will call `applicationContext.newImageLoader()` to
         * obtain an ImageLoader.
         * We impl in :app:HiltApp.Kt the SingletonImageLoader.Factory so it will be used in all app modules
         * @see <a href="https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt">Coil</a>
         */
        @Provides
        @Singleton
        fun imageLoader(
            // We specifically request dagger.Lazy here, so that it's not instantiated from Dagger.
            okHttpCallFactory: dagger.Lazy<Call.Factory>,
            @ApplicationContext application: Context,
        ): ImageLoader =
            ImageLoader.Builder(application)
                .components {
                    add(AnimatedImageDecoder.Factory()) // Gif support
                    add(SvgDecoder.Factory()) // Svg support
                    add(
                        OkHttpNetworkFetcherFactory(
                            callFactory = {
                                okHttpCallFactory.get()
                            },
                        ),
                    )
                }
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build()

    }
}