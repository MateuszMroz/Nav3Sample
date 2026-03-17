package com.mudita.features.order.presentation

import androidx.lifecycle.ViewModel
import com.mudita.core.domain.usecase.GetOrderByIdUseCase
import com.mudita.libraries.navigation.NavAction.NavigateUp
import com.mudita.libraries.navigation.NavActionsEmitter
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * OrderDetailViewModel - manages order detail state and navigation
 * 
 * Uses delegation pattern (by NavActionsEmitter()) for clean navigation API
 * ViewModels emit NavActions, UI layer (NavActionsEffect) handles actual navigation
 */
class OrderDetailViewModel(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel(), NavActionsEmitter by NavActionsEmitter() {
    
    private val _state = MutableStateFlow(OrderDetailContract.initialState())
    val state: StateFlow<OrderDetailState> = _state.asStateFlow()
    
    fun handleIntent(intent: OrderDetailIntent) {
        when (intent) {
            is OrderDetailIntent.LoadOrder -> loadOrder(intent.orderId)
            is OrderDetailIntent.OnBackClick -> onBackClick()
            is OrderDetailIntent.OnEditClick -> onEditClick()
        }
    }
    
    private fun loadOrder(orderId: String) = intent {
        _state.update { it.copy(isLoading = true, error = null) }

        try {
            val order = getOrderByIdUseCase(orderId)
            if (order != null) {
                _state.update {
                    it.copy(order = order, isLoading = false, error = null)
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Nie znaleziono zamówienia"
                    )
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Nieznany błąd"
                )
            }
        }
    }
    
    private fun onBackClick() = intent {
        emitNavAction(NavigateUp)
    }
    
    private fun onEditClick() = intent {
        // Navigation will be handled in Compose layer
    }
}
