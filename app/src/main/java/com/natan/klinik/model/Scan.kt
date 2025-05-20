package com.natan.klinik.model

import com.google.gson.annotations.SerializedName

data class Scan(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("user_id")
    val userId: Int,
    @field:SerializedName("photo")
    val photo: String,
    @field:SerializedName("created_at")
    val createdAt: String
)