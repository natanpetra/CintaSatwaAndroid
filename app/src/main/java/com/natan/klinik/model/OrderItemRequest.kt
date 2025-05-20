package com.natan.klinik.model

data class OrderItemRequest(
    val product_id: Int,
    val quantity: Int
)