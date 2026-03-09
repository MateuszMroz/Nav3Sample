package com.mudita.features.order.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudita.core.domain.usecase.GetOrdersUseCase
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.features.order.navigation.Order.Add
import com.mudita.features.order.navigation.Order.Detail
import com.mudita.libraries.navigation.NavAction.NavigateTo
import com.mudita.libraries.navigation.NavActionsEmitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * OrderListViewModel - manages order list state and navigation
 * 
 * Uses delegation pattern (by NavActionsEmitter()) for clean navigation API
 * ViewModels emit NavActions, UI layer (NavActionsEffect) handles actual navigation
 */
class OrderListViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val authRepository: AuthRepository
) : ViewModel(), NavActionsEmitter by NavActionsEmitter() {
    
    private val _state = MutableStateFlow(OrderListContract.initialState())
    val state: StateFlow<OrderListState> = _state.asStateFlow()
    
    init {
        handleIntent(OrderListIntent.LoadOrders)
    }
    
    fun handleIntent(intent: OrderListIntent) {
        when (intent) {
            is OrderListIntent.LoadOrders -> loadOrders()
            is OrderListIntent.OnOrderClick -> onOrderClick(intent.orderId)
            is OrderListIntent.OnAddOrderClick -> onAddOrderClick()
            is OrderListIntent.OnLogout -> onLogout()
        }
    }
    
    private fun loadOrders() {
        viewModelScope.launch {
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
    }
    
    private fun onOrderClick(orderId: String) {
        viewModelScope.launch {
            emitNavAction(NavigateTo(Detail(orderId)))
        }
    }
    
    private fun onAddOrderClick() {
        viewModelScope.launch {
            emitNavAction(NavigateTo(Add))
        }
    }
    
    private fun onLogout() {
        authRepository.logout()
    }
}
