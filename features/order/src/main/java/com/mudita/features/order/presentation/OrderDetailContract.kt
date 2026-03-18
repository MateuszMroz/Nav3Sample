package com.mudita.features.order.presentation

import com.mudita.core.domain.model.Order

sealed interface OrderDetailContract {
    sealed interface Intent: OrderDetailContract {
        data class LoadOrder(val orderId: String) : Intent
        data object OnBackClick : Intent
        data class OnEditClick(val orderId: String) : Intent
    }

    sealed interface Effect: OrderDetailContract {
        data object NavigateBack : Effect
        data class NavigateToEdit(val orderId: String) : Effect
        data class ShowError(val message: String) : Effect
    }
}

data class OrderDetailState(
    val order: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null
): OrderDetailContract
