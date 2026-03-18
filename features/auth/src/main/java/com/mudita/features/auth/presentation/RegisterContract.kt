package com.mudita.features.auth.presentation

sealed interface RegisterContract {
    sealed interface Intent : RegisterContract {
        data class EmailChanged(val email: String) : Intent
        data class PasswordChanged(val password: String) : Intent
        data class ConfirmPasswordChanged(val password: String) : Intent
        data object RegisterClicked : Intent
        data object BackClicked : Intent
    }

    sealed interface Effect : RegisterContract {
        data object NavigateUp : Effect
    }
}

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : RegisterContract
