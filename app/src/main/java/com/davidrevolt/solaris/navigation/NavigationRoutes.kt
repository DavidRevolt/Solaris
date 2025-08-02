package com.davidrevolt.solaris.navigation

import kotlinx.serialization.Serializable

interface NavigationRoute

@Serializable
data class Home(val locationIdToFocusOn: String? = null) : NavigationRoute

@Serializable
object Locations : NavigationRoute

@Serializable
data class SyncUi(val locationId: String) : NavigationRoute