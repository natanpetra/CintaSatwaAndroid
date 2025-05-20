package com.natan.klinik.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Reservation(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("pet_name")
    val petName: String? = null,

    @field:SerializedName("pet_type")
    val petType: String? = null,

    @field:SerializedName("reservation_date")
    val reservationDate: String? = null,

    @field:SerializedName("reservation_time")
    val reservationTime: String? = null,

    @field:SerializedName("symptoms")
    val symptoms: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("doctor_notes")
    val doctorNotes: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null
) : Serializable