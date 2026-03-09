package com.mudita.features.order.presentation

import com.mudita.core.domain.model.Order

data class OrderListState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface OrderListIntent {
    data object LoadOrders : OrderListIntent
    data class OnOrderClick(val orderId: String) : OrderListIntent
    data object OnAddOrderClick : OrderListIntent
    data object OnLogout : OrderListIntent
}

sealed interface OrderListEffect {
    data class NavigateToOrderDetail(val orderId: String) : OrderListEffect
    data object NavigateToAddOrder : OrderListEffect
    data class ShowError(val message: String) : OrderListEffect
}

class OrderListContract {
    companion object {
        fun initialState() = OrderListState()
    }
}
