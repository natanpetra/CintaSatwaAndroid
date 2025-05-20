package com.natan.klinik.model

import com.google.gson.annotations.SerializedName

data class OrderItem(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val subtotal: Int,
    val product: ProductItem? // Jika ada data product
)
