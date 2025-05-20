package com.natan.klinik.model

import com.google.gson.annotations.SerializedName

data class Profile(

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("is_scan")
	val isScan: Int? = null,

	@field:SerializedName("company_id")
	val companyId: Any? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("token_api")
	val tokenApi: String? = null,

	@field:SerializedName("region_id")
	val regionId: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any? = null,

	@field:SerializedName("region_type")
	val regionType: Int? = null,

	@field:SerializedName("identity_number")
	val identityNumber: Long? = null,

	@field:SerializedName("identity_image_url")
	val identityImageUrl: String? = null,

	@field:SerializedName("npwp_number")
	val npwpNumber: Any? = null,

	@field:SerializedName("notification_channel_id")
	val notificationChannelId: Any? = null,

	@field:SerializedName("token_email_verification")
	val tokenEmailVerification: Any? = null,

	@field:SerializedName("identity_image")
	val identityImage: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("phone")
	val phone: Long? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("fax")
	val fax: Any? = null,

	@field:SerializedName("email")
	val email: String? = null
)
