package com.mudita.features.order.navigation

import com.mudita.core.navigation.NavRoute
import kotlinx.serialization.Serializable

/**
 * Order screens
 */
@Serializable
sealed interface Order : NavRoute {
    /**
     * Order list - entry point for order flow
     */
    @Serializable
    data object List : Order

    /**
     * Order detail screen
     */
    @Serializable
    data class Detail(val orderId: String) : Order

    /**
     * Add new order screen
     */
    @Serializable
    data object Add : Order
}
