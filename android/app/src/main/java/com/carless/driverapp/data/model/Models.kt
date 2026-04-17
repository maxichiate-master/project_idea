package com.carless.driverapp.data.model

data class RegisterRequest(val name: String, val email: String, val phone: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val name: String,
    val driver: Boolean
)

data class UserInfo(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String
)

data class DriverProfile(
    val id: Long,
    val user: UserInfo,
    val status: String,
    val online: Boolean,
    val currentZone: String?
)

data class DriverUpgradeRequest(val dni: String, val licenseNumber: String)
data class DriverOnlineRequest(val online: Boolean, val zone: String?)

data class TripCreateRequest(
    val pickupAddress: String,
    val destinationAddress: String,
    val zone: String
)

data class TripResponse(
    val id: Long,
    val passenger: UserInfo?,
    val driver: UserInfo?,
    val pickupAddress: String,
    val destinationAddress: String,
    val zone: String,
    val status: String,
    val requestedAt: String,
    val acceptedAt: String?,
    val completedAt: String?
)

data class RatingRequest(val tripId: Long, val score: Int, val comment: String?)
