package com.mudita.features.auth.presentation

import androidx.lifecycle.ViewModel
import com.mudita.features.auth.domain.AuthRepository
import com.mudita.libraries.viewmodel.intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = Channel<RegisterContract.Effect>(Channel.BUFFERED)
    val effect: Flow<RegisterContract.Effect> = _effect.receiveAsFlow()

    fun handleIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.EmailChanged -> intent {
                _state.update { it.copy(email = intent.email, error = null) }
            }

            is RegisterContract.Intent.PasswordChanged -> intent {
                _state.update { it.copy(password = intent.password, error = null) }
            }

            is RegisterContract.Intent.ConfirmPasswordChanged -> intent {
                _state.update { it.copy(confirmPassword = intent.password, error = null) }
            }

            is RegisterContract.Intent.RegisterClicked -> register()
            is RegisterContract.Intent.BackClicked -> navigateBack()
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
        _effect.send(RegisterContract.Effect.NavigateUp)
    }
}
