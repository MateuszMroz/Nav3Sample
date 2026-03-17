package com.mudita.features.auth.presentation

import androidx.lifecycle.ViewModel
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.features.auth.navigation.Auth
import com.mudita.libraries.navigation.NavAction
import com.mudita.libraries.navigation.NavActionsEmitter
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object LoginClicked : LoginIntent
    data object RegisterClicked : LoginIntent
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel(), NavActionsEmitter by NavActionsEmitter() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> {
                _state.update { it.copy(email = intent.email, error = null) }
            }

            is LoginIntent.PasswordChanged -> {
                _state.update { it.copy(password = intent.password, error = null) }
            }

            is LoginIntent.LoginClicked -> login()
            is LoginIntent.RegisterClicked -> navigateToRegister()
        }
    }

    private fun login() = intent {
        _state.update { it.copy(isLoading = true, error = null) }

        val result = authRepository.login(
            username = _state.value.email,
            password = _state.value.password
        )

        result.fold(
            onSuccess = {
                // Navigation będzie obsłużona przez NavRoot
                // który obserwuje authRepository.isAuthenticated
            },
            onFailure = {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Nieprawidłowy email lub hasło"
                    )
                }
            }
        )
    }

    private fun navigateToRegister() = intent {
        emitNavAction(NavAction.NavigateTo(Auth.Register))
    }
}
