package com.carless.driverapp.ui.passenger

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carless.driverapp.data.api.ApiClient
import com.carless.driverapp.data.model.TripCreateRequest
import com.carless.driverapp.data.model.TripResponse
import kotlinx.coroutines.launch

class PassengerViewModel : ViewModel() {

    val tripCreated = MutableLiveData<Result<TripResponse>>()
    val activeTrip = MutableLiveData<TripResponse?>()
    val isLoading = MutableLiveData(false)
    private val api = ApiClient.getService()

    fun requestTrip(pickup: String, destination: String, zone: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.createTrip(TripCreateRequest(pickup, destination, zone))
                if (response.isSuccessful && response.body() != null) {
                    tripCreated.value = Result.success(response.body()!!)
                } else {
                    tripCreated.value = Result.failure(Exception("Could not create trip"))
                }
            } catch (e: Exception) {
                tripCreated.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun pollActiveTrip() {
        viewModelScope.launch {
            try {
                val response = api.getActiveTrip()
                activeTrip.value = if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                activeTrip.value = null
            }
        }
    }

    fun cancelTrip(tripId: Long) {
        viewModelScope.launch {
            try { api.cancelTrip(tripId) } catch (e: Exception) { /* ignore */ }
        }
    }
}
