package com.mudita.features.auth.presentation

import androidx.lifecycle.ViewModel
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    private val _effect = Channel<LoginContract.Effect>(Channel.BUFFERED)
    val effect: Flow<LoginContract.Effect> = _effect.receiveAsFlow()

    fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.EmailChanged -> intent {
                _state.update { it.copy(email = intent.email, error = null) }
            }

            is LoginContract.Intent.PasswordChanged -> intent {
                _state.update { it.copy(password = intent.password, error = null) }
            }

            is LoginContract.Intent.LoginClicked -> login()
            is LoginContract.Intent.RegisterClicked -> navigateToRegister()
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
                _state.update { it.copy(isLoading = false, error = null) }
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
        _effect.send(LoginContract.Effect.NavigateToRegister)
    }
}
