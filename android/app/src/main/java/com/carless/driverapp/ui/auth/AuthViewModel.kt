package com.carless.driverapp.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carless.driverapp.data.api.ApiClient
import com.carless.driverapp.data.model.AuthResponse
import com.carless.driverapp.data.model.LoginRequest
import com.carless.driverapp.data.model.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    val authResult = MutableLiveData<Result<AuthResponse>>()
    val isLoading = MutableLiveData(false)
    private val api = ApiClient.getService()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    authResult.value = Result.success(response.body()!!)
                } else {
                    authResult.value = Result.failure(Exception("Invalid credentials"))
                }
            } catch (e: Exception) {
                authResult.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.register(RegisterRequest(name, email, phone, password))
                if (response.isSuccessful && response.body() != null) {
                    authResult.value = Result.success(response.body()!!)
                } else {
                    authResult.value = Result.failure(Exception("Registration failed. Email or phone may already be in use."))
                }
            } catch (e: Exception) {
                authResult.value = Result.failure(Exception("Connection error"))
            } finally {
                isLoading.value = false
            }
        }
    }
}
