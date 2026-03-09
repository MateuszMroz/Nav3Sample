package com.mudita.core.data.repository

import com.mudita.core.domain.model.Order
import com.mudita.core.domain.model.OrderStatus
import com.mudita.core.domain.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl : OrderRepository {
    
    private val mockOrders = mutableListOf(
        Order(
            id = "1",
            title = "Zamówienie #001",
            description = "Laptopy dla biura",
            status = OrderStatus.CONFIRMED,
            totalAmount = 15000.0
        ),
        Order(
            id = "2",
            title = "Zamówienie #002",
            description = "Meble biurowe",
            status = OrderStatus.IN_PROGRESS,
            totalAmount = 8500.0
        ),
        Order(
            id = "3",
            title = "Zamówienie #003",
            description = "Sprzęt IT",
            status = OrderStatus.PENDING,
            totalAmount = 12000.0
        )
    )
    
    override fun getOrders(): Flow<List<Order>> = flow {
        delay(500)
        emit(mockOrders.toList())
    }
    
    override suspend fun getOrderById(id: String): Order? {
        delay(300)
        return mockOrders.find { it.id == id }
    }
    
    override suspend fun createOrder(order: Order): Result<Order> {
        delay(500)
        mockOrders.add(order)
        return Result.success(order)
    }
    
    override suspend fun updateOrder(order: Order): Result<Order> {
        delay(500)
        val index = mockOrders.indexOfFirst { it.id == order.id }
        if (index != -1) {
            mockOrders[index] = order
            return Result.success(order)
        }
        return Result.failure(Exception("Order not found"))
    }
    
    override suspend fun deleteOrder(id: String): Result<Unit> {
        delay(500)
        val removed = mockOrders.removeIf { it.id == id }
        return if (removed) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Order not found"))
        }
    }
}
