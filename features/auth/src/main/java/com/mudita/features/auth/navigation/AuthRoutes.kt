package com.mudita.features.auth.navigation

import com.mudita.core.navigation.NavRoute
import kotlinx.serialization.Serializable

/**
 * Auth screens
 */
sealed interface Auth : NavRoute {
    /**
     * Login screen - entry point for auth flow
     */
    @Serializable
    data object Login : Auth

    /**
     * Register screen
     */
    @Serializable
    data object Register : Auth
}
