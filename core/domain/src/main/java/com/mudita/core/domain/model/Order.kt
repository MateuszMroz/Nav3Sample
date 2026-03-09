package com.mudita.core.domain.model

data class Order(
    val id: String,
    val title: String,
    val description: String,
    val status: OrderStatus,
    val totalAmount: Double
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
