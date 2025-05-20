package com.natan.klinik.model

data class OrderResponse(
    val success: Boolean,
    val data: List<Order>
)