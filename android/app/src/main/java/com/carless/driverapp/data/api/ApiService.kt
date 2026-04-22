package com.carless.driverapp.data.api

import com.carless.driverapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>

    @PUT("auth/fcm-token")
    suspend fun updateFcmToken(@Body body: Map<String, String>): Response<Unit>

    @POST("driver/upgrade")
    suspend fun upgradeToDriver(@Body req: DriverUpgradeRequest): Response<DriverProfile>

    @PUT("driver/status")
    suspend fun setDriverStatus(@Body req: DriverOnlineRequest): Response<DriverProfile>

    @GET("driver/requests")
    suspend fun getAvailableRequests(): Response<List<TripResponse>>

    @POST("driver/requests/{id}/accept")
    suspend fun acceptTrip(@Path("id") id: Long): Response<TripResponse>

    @POST("driver/requests/{id}/start")
    suspend fun startTrip(@Path("id") id: Long): Response<TripResponse>

    @POST("driver/requests/{id}/complete")
    suspend fun completeTrip(@Path("id") id: Long): Response<TripResponse>

    @POST("trips")
    suspend fun createTrip(@Body req: TripCreateRequest): Response<TripResponse>

    @GET("trips/active")
    suspend fun getActiveTrip(): Response<TripResponse>

    @GET("trips/{id}")
    suspend fun getTrip(@Path("id") id: Long): Response<TripResponse>

    @POST("trips/{id}/cancel")
    suspend fun cancelTrip(@Path("id") id: Long): Response<TripResponse>

    @POST("trips/rate")
    suspend fun rateTrip(@Body req: RatingRequest): Response<Unit>
}
