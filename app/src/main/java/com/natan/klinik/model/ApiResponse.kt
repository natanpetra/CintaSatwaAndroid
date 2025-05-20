package com.natan.klinik.model

data class ApiResponse(
    val status: String,
    val message: String? = null,
    val order_id: Int? = null,
    val total_price: Double? = null,
    val error: String? = null
)