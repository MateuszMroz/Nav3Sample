package com.mudita.features.order.presentation

import androidx.lifecycle.ViewModel
import com.mudita.core.domain.usecase.GetOrdersUseCase
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.features.order.presentation.OrderListContract.Effect
import com.mudita.features.order.presentation.OrderListContract.Intent
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * OrderListViewModel - manages order list state and navigation
 *
 * Uses delegation pattern (by NavActionsEmitter()) for clean navigation API
 * ViewModels emit NavActions, UI layer (NavActionsEffect) handles actual navigation
 */
class OrderListViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderListState())
    val state: StateFlow<OrderListState> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    init {
        handleIntent(Intent.LoadOrders)
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadOrders -> loadOrders()
            is Intent.OnOrderClick -> onOrderClick(intent.orderId)
            is Intent.OnAddOrderClick -> onAddOrderClick()
            is Intent.OnLogout -> onLogout()
        }
    }

    private fun loadOrders() = intent {
        getOrdersUseCase()
            .onStart {
                _state.update { it.copy(isLoading = true, error = null) }
            }
            .catch { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Nieznany błąd"
                    )
                }
            }
            .collect { orders ->
                _state.update {
                    it.copy(
                        orders = orders,
                        isLoading = false,
                        error = null
                    )
                }
            }
    }

    private fun onOrderClick(orderId: String) = intent {
        _effect.send(Effect.NavigateToOrderDetail(orderId))
    }

    private fun onAddOrderClick() = intent {
        _effect.send(Effect.NavigateToAddOrder)
    }

    private fun onLogout() = intent {
        authRepository.logout()
    }
}
