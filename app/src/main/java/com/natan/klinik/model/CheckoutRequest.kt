package com.natan.klinik.model

data class CheckoutRequest(
    val user_id: Int,
    val items: List<OrderItemRequest>,
    val total_price: Double
)