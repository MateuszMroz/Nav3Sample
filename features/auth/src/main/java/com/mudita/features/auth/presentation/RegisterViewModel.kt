package com.mudita.features.auth.presentation

import androidx.lifecycle.ViewModel
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.libraries.navigation.NavAction
import com.mudita.libraries.navigation.NavActionsEmitter
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface RegisterIntent {
    data class EmailChanged(val email: String) : RegisterIntent
    data class PasswordChanged(val password: String) : RegisterIntent
    data class ConfirmPasswordChanged(val password: String) : RegisterIntent
    data object RegisterClicked : RegisterIntent
    data object BackClicked : RegisterIntent
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel(), NavActionsEmitter by NavActionsEmitter() {
    
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()
    
    fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.EmailChanged -> {
                _state.update { it.copy(email = intent.email, error = null) }
            }
            is RegisterIntent.PasswordChanged -> {
                _state.update { it.copy(password = intent.password, error = null) }
            }
            is RegisterIntent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = intent.password, error = null) }
            }
            is RegisterIntent.RegisterClicked -> register()
            is RegisterIntent.BackClicked -> navigateBack()
        }
    }
    
    private fun register() = intent {
        val currentState = _state.value

        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(error = "Hasła nie są identyczne") }
            return@intent
        }

        _state.update { it.copy(isLoading = true, error = null) }

        val result = authRepository.register(
            username = currentState.email,
            password = currentState.password
        )

        result.fold(
            onSuccess = {
                // Navigation będzie obsłużona przez NavRoot
            },
            onFailure = {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Rejestracja nie powiodła się"
                    )
                }
            }
        )
    }
    
    private fun navigateBack() = intent {
        emitNavAction(NavAction.NavigateUp)
    }
}
