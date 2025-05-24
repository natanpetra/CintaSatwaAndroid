package com.natan.klinik.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Ectoparasite(

	@field:SerializedName("symptoms")
	val symptoms: String? = null,

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Serializable
