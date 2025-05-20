package com.natan.klinik.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Guide(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("image_url")
	val image: String? = null,
) : Serializable
