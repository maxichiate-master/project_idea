package com.carless.driverapp

import android.app.Application
import com.carless.driverapp.data.api.ApiClient

class DriverApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}
