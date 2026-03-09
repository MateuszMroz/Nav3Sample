package com.mudita.core.navigation

import androidx.navigation3.runtime.NavKey
import com.mudita.libraries.navigation.Route

/**
 * Bridge between Route abstraction and Navigation 3's NavKey.
 * All app routes should implement this interface.
 * 
 * Usage:
 * ```
 * @Serializable
 * data class DetailRoute(val id: String) : NavRoute
 * ```
 */
interface NavRoute : Route, NavKey
