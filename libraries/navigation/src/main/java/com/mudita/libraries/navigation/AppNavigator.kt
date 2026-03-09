package com.mudita.libraries.navigation

/**
 * Interface for navigation operations.
 * Pure Kotlin abstraction - no framework dependencies.
 */
interface AppNavigator {
    fun navigateTo(route: Route)
    fun navigateUp()
    fun popBackTo(route: Route, inclusive: Boolean = false)
}
