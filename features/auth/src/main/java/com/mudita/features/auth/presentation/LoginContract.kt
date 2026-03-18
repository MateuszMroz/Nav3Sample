package com.mudita.features.auth.presentation

sealed interface LoginContract {

    sealed interface Intent : LoginContract {
        data class EmailChanged(val email: String) : Intent
        data class PasswordChanged(val password: String) : Intent
        data object LoginClicked : Intent
        data object RegisterClicked : Intent
    }

    sealed interface Effect : LoginContract {
        data object NavigateToRegister : Effect
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : LoginContract
