package com.natan.klinik.network

import com.natan.klinik.model.ApiResponse
import com.natan.klinik.model.CheckoutRequest
import com.natan.klinik.model.Clinic
import com.natan.klinik.model.DoctorItem
import com.natan.klinik.model.Ectoparasite
import com.natan.klinik.model.Guide
import com.natan.klinik.model.OrderResponse
import com.natan.klinik.model.ProductItem
import com.natan.klinik.model.Profile
import com.natan.klinik.model.Reservation
import com.natan.klinik.model.ScanResponse
import com.natan.klinik.model.SubmitScanResponse
import com.natan.klinik.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("customer/sign-in") // Sesuaikan dengan endpoint API kamu
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Profile>

    @FormUrlEncoded
    @POST("customer/sign-up") // Sesuaikan dengan endpoint API kamu
    fun register(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<Profile>

    @GET("product") // Sesuaikan dengan endpoint API kamu
    fun getProduct(
    ): Call<List<ProductItem>>

    @GET("clinic") // Sesuaikan dengan endpoint API kamu
    fun getClinic(
    ): Call <Clinic>

    @GET("doctors") // Sesuaikan dengan endpoint API kamu
    fun getDoctor(
    ): Call<List<DoctorItem>>

    @GET("dog-care-guides") // Sesuaikan dengan endpoint API kamu
    fun getGuide(
    ): Call<List<Guide>>

    @GET("ectoparasite-diseases") // Sesuaikan dengan endpoint API kamu
    fun getEctoparasite(
    ): Call<List<Ectoparasite>>

    @POST("checkout")
    fun checkout(@Body request: CheckoutRequest): Call<ApiResponse>

    @GET("orders/{user_id}")
    fun getOrderHistory(
        @Path("user_id") userId: Int
    ): Call<OrderResponse>

    @GET("scan/{user_id}")
    fun getScanHistory(
        @Path("user_id") userId: Int
    ): Call<ScanResponse>

    @Multipart
    @POST("submit-scan")
    fun submitScan(
        @Part photo: MultipartBody.Part,
        @Part("user_id") userId: RequestBody
    ): Call<SubmitScanResponse>

    @FormUrlEncoded
    @POST("reservations/create")
    fun createReservation(
        @Field("user_id") userId: Int,
        @Field("pet_name") petName: String,
        @Field("pet_type") petType: String,
        @Field("reservation_date") reservationDate: String,
        @Field("reservation_time") reservationTime: String,
        @Field("symptoms") symptoms: String
    ): Call<ApiResponse>

    // Endpoint untuk mendapatkan riwayat reservasi (sesuaikan path dengan API Anda)
    @GET("reservations/{user_id}")
    fun getReservationHistory(
        @Path("user_id") userId: Int
    ): Call<List<Reservation>>

    @Multipart
    @POST("customer/update-thumbnail") // ganti dengan endpoint kamu
    fun uploadThumbnail(
        @Part("user_id") userId: RequestBody,
        @Part thumbnail: MultipartBody.Part
    ): Call<UploadResponse>
}
