package com.carless.driverapp.ui.driver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carless.driverapp.data.api.ApiClient
import com.carless.driverapp.data.model.*
import kotlinx.coroutines.launch

class DriverViewModel : ViewModel() {

    val driverProfile = MutableLiveData<Result<DriverProfile>>()
    val availableTrips = MutableLiveData<List<TripResponse>>()
    val tripAction = MutableLiveData<Result<TripResponse>>()
    val isLoading = MutableLiveData(false)
    private val api = ApiClient.getService()

    fun applyForDriver(dni: String, licenseNumber: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.upgradeToDriver(DriverUpgradeRequest(dni, licenseNumber))
                if (response.isSuccessful && response.body() != null) {
                    driverProfile.value = Result.success(response.body()!!)
                } else {
                    driverProfile.value = Result.failure(Exception("Application failed"))
                }
            } catch (e: Exception) {
                driverProfile.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun setOnlineStatus(online: Boolean, zone: String? = null) {
        viewModelScope.launch {
            try { api.setDriverStatus(DriverOnlineRequest(online, zone)) } catch (e: Exception) { /* ignore */ }
        }
    }

    fun loadAvailableTrips() {
        viewModelScope.launch {
            try {
                val response = api.getAvailableRequests()
                availableTrips.value = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
            } catch (e: Exception) {
                availableTrips.value = emptyList()
            }
        }
    }

    fun acceptTrip(tripId: Long) {
        viewModelScope.launch {
            try {
                val response = api.acceptTrip(tripId)
                if (response.isSuccessful && response.body() != null) {
                    tripAction.value = Result.success(response.body()!!)
                } else {
                    tripAction.value = Result.failure(Exception("Trip no longer available"))
                }
            } catch (e: Exception) {
                tripAction.value = Result.failure(Exception("Connection error"))
            }
        }
    }

    fun startTrip(tripId: Long) {
        viewModelScope.launch {
            try {
                val response = api.startTrip(tripId)
                if (response.isSuccessful && response.body() != null) tripAction.value = Result.success(response.body()!!)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun completeTrip(tripId: Long) {
        viewModelScope.launch {
            try {
                val response = api.completeTrip(tripId)
                if (response.isSuccessful && response.body() != null) tripAction.value = Result.success(response.body()!!)
            } catch (e: Exception) { /* ignore */ }
        }
    }
}
