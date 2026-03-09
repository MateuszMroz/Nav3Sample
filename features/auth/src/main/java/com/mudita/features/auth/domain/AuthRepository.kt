package com.mudita.features.auth.domain

import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for authentication operations
 */
interface AuthRepository {
    val isAuthenticated: StateFlow<Boolean>
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun register(username: String, password: String): Result<Unit>
    fun logout()
    suspend fun checkAuthStatus()
}
