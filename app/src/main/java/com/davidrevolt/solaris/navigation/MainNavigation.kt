package com.davidrevolt.solaris.navigation

import android.app.Activity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davidrevolt.feature.home.HomeScreen
import com.davidrevolt.feature.home.HomeViewModel
import com.davidrevolt.feature.locations.LocationsScreen
import com.davidrevolt.feature.sync_ui.SyncUiScreen
import com.davidrevolt.feature.sync_ui.SyncUiViewModel

@Composable
fun MainNavigation() {
    // Create a back stack, specifying the route the app should start with.
    val backStack = remember { mutableStateListOf<NavigationRoute>(Home()) }
    var setLightAppearanceBars by remember { mutableStateOf(true) } // true: system bars light, icons are BLACK

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            /*
             * By default, ViewModels are scoped to the nearest ViewModelStoreOwner [Activity or Fragment]
             * rememberSavedStateNavEntryDecorator ->
             * This means the ViewModel is created when the NavEntry is added to the back stack,
             * and cleared when it's removed [scope a ViewModel to a specific NavEntry].*/
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = { // When content is added to the back stack: Slide in from right
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = { // When content is removed from the back stack: Slide in from left
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        entryProvider = entryProvider {
            entry<Home> { entry ->
                setLightAppearanceBars = false
                // Init viewmodel here so we inject args straight to the viewmodel
                val viewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(entry.locationIdToFocusOn)
                    }
                )
                HomeScreen(
                    navigateToLocationsManagement = { backStack.add(Locations) },
                    viewModel = viewModel
                )
            }
            entry<Locations> { entry ->
                setLightAppearanceBars = true
                LocationsScreen(
                    onBackClick = backStack::removeLastOrNull,
                    onLocationClick = { locationId ->
                        backStack.removeAll { it is Home }
                        backStack.add(Home(locationId))
                        backStack.removeAll { it !is Home }
                    },
                    onLocationAddedEffect = { locationId -> backStack.add(SyncUi(locationId)) })
            }
            entry<SyncUi> { entry ->
                setLightAppearanceBars = true
                val viewModel = hiltViewModel<SyncUiViewModel, SyncUiViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(entry.locationId)
                    }
                )
                SyncUiScreen(
                    onBackClick = backStack::removeLastOrNull,
                    viewModel = viewModel
                )
            }
        },
    )

    val view = LocalView.current
    val window = (view.context as Activity).window
    val windowInsetsController =
        WindowCompat.getInsetsController(window, window.decorView)
    LaunchedEffect(setLightAppearanceBars) {
        windowInsetsController.isAppearanceLightNavigationBars = setLightAppearanceBars
        windowInsetsController.isAppearanceLightStatusBars = setLightAppearanceBars
    }
}
