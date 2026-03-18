package com.mudita.nav3sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.mudita.compose.auth.AuthNavigation
import com.mudita.compose.navigation.clearWith
import com.mudita.compose.navigation.navigateUp
import com.mudita.compose.navigation.rememberNavBackStack
import com.mudita.compose.order.OrderNavigation
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
    val backStack = rememberNavBackStack<RootRoutes>(RootRoutes.Auth)

    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            backStack.clearWith(RootRoutes.List)
        } else {
            backStack.clearWith(RootRoutes.Auth)
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.navigateUp() },
        entryProvider = entryProvider {
            entry<RootRoutes.Auth> {
                AuthNavigation()
            }

            entry<RootRoutes.List> {
                OrderNavigation()
            }
        }
    )
}
