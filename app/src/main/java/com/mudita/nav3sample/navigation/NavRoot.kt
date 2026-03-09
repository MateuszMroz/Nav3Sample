package com.mudita.nav3sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mudita.compose.auth.AuthNavigation
import com.mudita.compose.navigation.rememberNavigator
import com.mudita.compose.order.OrderNavigation
import com.mudita.features.auth.navigation.Auth
import com.mudita.features.order.navigation.Order
import com.mudita.nav3sample.presentation.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Root navigation - manages top-level flow switching.
 * 
 * Uses first screen of each flow as entry point.
 * Internal flow structure is managed by nested graphs.
 */
@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    
    // Use first screen of each flow as entry point
    val startDestination = if (isAuthenticated) Order.List else Auth.Login
    val rootNavigator = rememberNavigator(startDestination = startDestination)

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            if (rootNavigator.backStack.lastOrNull() !is Order.List) {
                rootNavigator.backStack.clear()
                rootNavigator.backStack.add(Order.List)
            }
        } else {
            if (rootNavigator.backStack.lastOrNull() !is Auth.Login) {
                rootNavigator.backStack.clear()
                rootNavigator.backStack.add(Auth.Login)
            }
        }
    }
    
    NavDisplay(
        backStack = rootNavigator.backStack,
        modifier = modifier,
        onBack = { rootNavigator.navigateUp() },
        entryProvider = entryProvider {
            entry<Auth.Login> {
                AuthNavigation()
            }

            entry<Order.List> {
                OrderNavigation()
            }
        }
    )
}
