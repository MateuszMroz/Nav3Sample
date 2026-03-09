package com.mudita.features.auth.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages authentication state.
 * In real app, this would interact with repository/API.
 */
class AuthManager {
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    fun login(email: String, password: String): Boolean {
        // Simple validation for demo
        if (email.isNotBlank() && password.length >= 4) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }
    
    fun register(email: String, password: String): Boolean {
        // Simple validation for demo
        if (email.isNotBlank() && password.length >= 4) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }
    
    fun logout() {
        _isAuthenticated.value = false
    }
    
    fun checkAuthStatus(): Boolean {
        return _isAuthenticated.value
    }
}
