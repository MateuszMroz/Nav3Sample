package com.mudita.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.mudita.core.navigation.NavRoute
import com.mudita.libraries.navigation.AppNavigator
import com.mudita.libraries.navigation.Route

/**
 * Navigator implementation using Navigation 3's rememberNavBackStack.
 * Lives in Compose layer - not in DI.
 */
@Stable
class Navigator(
    val backStack: NavBackStack<NavKey>
) : AppNavigator {

    override fun navigateTo(route: Route) {
        require(route is NavKey) { "Route must implement NavKey (use NavRoute)" }
        backStack.add(route)
    }

    override fun navigateUp() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    override fun popBackTo(route: Route, inclusive: Boolean) {
        require(route is NavKey) { "Route must implement NavKey (use NavRoute)" }
        
        val index = backStack.indexOfLast { it == route }
        if (index != -1) {
            val removeFrom = if (inclusive) index else index + 1
            while (backStack.size > removeFrom) {
                backStack.removeLastOrNull()
            }
        }
    }
}

/**
 * Creates Navigator with persistent back stack.
 * Survives configuration changes and handles system back.
 */
@Composable
fun rememberNavigator(startDestination: NavRoute): Navigator {
    val backStack = rememberNavBackStack(startDestination)
    return remember { Navigator(backStack) }
}
