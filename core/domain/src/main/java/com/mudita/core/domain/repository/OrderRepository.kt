package com.mudita.core.domain.repository

import com.mudita.core.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    
    suspend fun getOrderById(id: String): Order?
    
    suspend fun createOrder(order: Order): Result<Order>
    
    suspend fun updateOrder(order: Order): Result<Order>
    
    suspend fun deleteOrder(id: String): Result<Unit>
}
