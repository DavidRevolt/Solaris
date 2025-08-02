package com.davidrevolt.solaris

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * HiltApp triggers Hilt's code generation
 * */
@HiltAndroidApp
class HiltApp : Application(), SingletonImageLoader.Factory {

    // For Coil custom configuration in :network:di:NetworkModule.kt
    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return imageLoader.get()
    }

}