package com.carless.driverapp.ui.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carless.driverapp.data.api.ApiClient
import com.carless.driverapp.data.model.TripResponse
import kotlinx.coroutines.launch

class TripViewModel : ViewModel() {

    val trip = MutableLiveData<TripResponse?>()
    val actionResult = MutableLiveData<Result<TripResponse>>()
    val isLoading = MutableLiveData(false)
    private val api = ApiClient.getService()

    fun pollTrip(tripId: Long) {
        viewModelScope.launch {
            try {
                val response = api.getTrip(tripId)
                trip.value = if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                // keep last known state on transient errors
            }
        }
    }

    fun cancelTrip(tripId: Long) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.cancelTrip(tripId)
                if (response.isSuccessful && response.body() != null) {
                    actionResult.value = Result.success(response.body()!!)
                } else {
                    actionResult.value = Result.failure(Exception("Could not cancel trip"))
                }
            } catch (e: Exception) {
                actionResult.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun startTrip(tripId: Long) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.startTrip(tripId)
                if (response.isSuccessful && response.body() != null) {
                    actionResult.value = Result.success(response.body()!!)
                } else {
                    actionResult.value = Result.failure(Exception("Could not start trip"))
                }
            } catch (e: Exception) {
                actionResult.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun completeTrip(tripId: Long) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.completeTrip(tripId)
                if (response.isSuccessful && response.body() != null) {
                    actionResult.value = Result.success(response.body()!!)
                } else {
                    actionResult.value = Result.failure(Exception("Could not complete trip"))
                }
            } catch (e: Exception) {
                actionResult.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }
}