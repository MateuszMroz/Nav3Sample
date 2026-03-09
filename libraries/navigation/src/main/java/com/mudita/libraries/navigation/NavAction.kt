package com.mudita.libraries.navigation

/**
 * Navigation actions emitted by ViewModels.
 */
sealed class NavAction {
    data object NavigateUp : NavAction()
    data class NavigateTo(val route: Route) : NavAction()
    data class PopBackTo(
        val route: Route,
        val inclusive: Boolean = false
    ) : NavAction()
}
