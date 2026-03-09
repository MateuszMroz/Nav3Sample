package com.mudita.nav3sample.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudita.features.auth.domain.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Main ViewModel - manages app-level state
 */
class MainViewModel(
    authRepository: AuthRepository
) : ViewModel() {
    
    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
