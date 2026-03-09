package com.mudita.core.domain.usecase

import com.mudita.core.domain.model.Order
import com.mudita.core.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(): Flow<List<Order>> {
        return orderRepository.getOrders()
    }
}
