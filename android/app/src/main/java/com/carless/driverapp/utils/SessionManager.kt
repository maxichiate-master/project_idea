package com.carless.driverapp.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("carless_prefs", Context.MODE_PRIVATE)

    fun saveAuthData(token: String, userId: Long, name: String, isDriver: Boolean) {
        prefs.edit()
            .putString("token", token)
            .putLong("userId", userId)
            .putString("name", name)
            .putBoolean("isDriver", isDriver)
            .apply()
    }

    fun getToken(): String? = prefs.getString("token", null)
    fun getUserId(): Long = prefs.getLong("userId", -1)
    fun getName(): String? = prefs.getString("name", null)
    fun isDriver(): Boolean = prefs.getBoolean("isDriver", false)
    fun isLoggedIn(): Boolean = getToken() != null

    fun setIsDriver(value: Boolean) {
        prefs.edit().putBoolean("isDriver", value).apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
