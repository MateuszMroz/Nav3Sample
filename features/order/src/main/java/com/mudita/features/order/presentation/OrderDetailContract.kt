package com.mudita.features.order.presentation

import com.mudita.core.domain.model.Order

data class OrderDetailState(
    val order: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface OrderDetailIntent {
    data class LoadOrder(val orderId: String) : OrderDetailIntent
    data object OnBackClick : OrderDetailIntent
    data object OnEditClick : OrderDetailIntent
}

sealed interface OrderDetailEffect {
    data object NavigateBack : OrderDetailEffect
    data class NavigateToEdit(val orderId: String) : OrderDetailEffect
    data class ShowError(val message: String) : OrderDetailEffect
}

class OrderDetailContract {
    companion object {
        fun initialState() = OrderDetailState()
    }
}
