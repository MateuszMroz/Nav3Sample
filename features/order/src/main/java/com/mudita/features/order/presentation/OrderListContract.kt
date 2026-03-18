package com.mudita.features.order.presentation

import com.mudita.core.domain.model.Order

sealed interface OrderListContract {
    sealed interface Intent: OrderListContract {
        data object LoadOrders : Intent
        data class OnOrderClick(val orderId: String) : Intent
        data object OnAddOrderClick : Intent
        data object OnLogout : Intent
    }

    sealed interface Effect: OrderListContract {
        data class NavigateToOrderDetail(val orderId: String) : Effect
        data object NavigateToAddOrder : Effect
        data class ShowError(val message: String) : Effect
    }
}

data class OrderListState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
): OrderListContract
