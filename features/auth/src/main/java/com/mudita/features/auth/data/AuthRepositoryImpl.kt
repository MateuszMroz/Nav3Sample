package com.mudita.features.auth.data

import com.mudita.features.auth.domain.AuthManager
import com.mudita.features.auth.domain.AuthRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementation of AuthRepository
 * Delegates to AuthManager for actual auth logic
 */
class AuthRepositoryImpl(
    private val authManager: AuthManager
) : AuthRepository {
    
    override val isAuthenticated: StateFlow<Boolean> = authManager.isAuthenticated
    
    override suspend fun login(username: String, password: String): Result<Unit> {
        return if (authManager.login(username, password)) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Login failed"))
        }
    }
    
    override suspend fun register(username: String, password: String): Result<Unit> {
        return if (authManager.register(username, password)) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Registration failed"))
        }
    }
    
    override fun logout() {
        authManager.logout()
    }
    
    override suspend fun checkAuthStatus() {
        authManager.checkAuthStatus()
    }
}
