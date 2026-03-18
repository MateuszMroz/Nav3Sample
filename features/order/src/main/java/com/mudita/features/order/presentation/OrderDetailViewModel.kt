package com.mudita.features.order.presentation

import androidx.lifecycle.ViewModel
import com.mudita.core.domain.usecase.GetOrderByIdUseCase
import com.mudita.features.order.presentation.OrderDetailContract.Effect
import com.mudita.features.order.presentation.OrderDetailContract.Intent
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * OrderDetailViewModel - manages order detail state and navigation
 *
 * Uses delegation pattern (by NavActionsEmitter()) for clean navigation API
 * ViewModels emit NavActions, UI layer (NavActionsEffect) handles actual navigation
 */
class OrderDetailViewModel(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailState())
    val state: StateFlow<OrderDetailState> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadOrder -> loadOrder(intent.orderId)
            is Intent.OnBackClick -> onBackClick()
            is Intent.OnEditClick -> onEditClick(intent.orderId)
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
        _effect.send(Effect.NavigateBack)
    }

    private fun onEditClick(orderId: String) = intent {
        _effect.send(Effect.NavigateToEdit(orderId))
    }
}
