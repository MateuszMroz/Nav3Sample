package com.mudita.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mudita.libraries.navigation.AppNavigator
import com.mudita.libraries.navigation.NavAction
import kotlinx.coroutines.flow.Flow

/**
 * Connects ViewModel navigation actions to Navigator.
 * Add this to every screen that uses navigation.
 * 
 * Usage:
 * ```
 * NavActionsEffect(
 *     actions = viewModel.navActions,
 *     navigator = navigator
 * )
 * ```
 */
@Composable
fun NavActionsEffect(
    actions: Flow<NavAction>,
    navigator: AppNavigator
) {
    LaunchedEffect(Unit) {
        actions.collect { action ->
            when (action) {
                is NavAction.NavigateUp -> navigator.navigateUp()
                is NavAction.NavigateTo -> navigator.navigateTo(action.route)
                is NavAction.PopBackTo -> navigator.popBackTo(action.route, action.inclusive)
            }
        }
    }
}
