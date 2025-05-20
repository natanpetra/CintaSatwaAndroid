package com.natan.klinik.model

import com.google.gson.annotations.SerializedName

data class Order(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("user_id")
    val userId: Int,
    @field:SerializedName("total_price")
    val totalPrice: Int,
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("order_items")
    val orderItems: List<OrderItem>
)