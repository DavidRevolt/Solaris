package com.davidrevolt.solaris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.davidrevolt.solaris.navigation.MainNavigation
import com.davidrevolt.solaris.ui.theme.SolarisTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val whiteIconsTransparentBackground = SystemBarStyle.dark(Color.Transparent.toArgb())
        enableEdgeToEdge(
            statusBarStyle = whiteIconsTransparentBackground,
            navigationBarStyle = whiteIconsTransparentBackground
        )
        val locale = Locale.forLanguageTag("en")
        Locale.setDefault(locale)

        if (BuildConfig.DEBUG) {
            Timber.plant(CustomDebugTree())
        }

        setContent {
            SolarisTheme {
                MainNavigation()
            }
        }
    }
}

class CustomDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        // Prefix to help identify Timber logs in logcat
        return "SolarisLogs:${super.createStackElementTag(element)}"
    }
}