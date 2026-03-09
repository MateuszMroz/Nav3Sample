package com.mudita.core.domain.usecase

import com.mudita.core.domain.model.Order
import com.mudita.core.domain.repository.OrderRepository

class GetOrderByIdUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(id: String): Order? {
        return orderRepository.getOrderById(id)
    }
}
