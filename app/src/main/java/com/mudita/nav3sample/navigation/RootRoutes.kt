package com.mudita.nav3sample.navigation

import com.mudita.core.navigation.NavRoute
import kotlinx.serialization.Serializable

sealed interface RootRoutes : NavRoute {
    /**
     * Login screen - entry point for auth flow
     */
    @Serializable
    data object Auth : RootRoutes

    /**
     * Order list - entry point for order flow
     */
    @Serializable
    data object List : RootRoutes
}
