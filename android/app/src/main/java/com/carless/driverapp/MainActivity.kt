package com.carless.driverapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.ui.auth.LoginActivity
import com.carless.driverapp.ui.driver.DriverHomeActivity
import com.carless.driverapp.ui.passenger.PassengerHomeActivity
import com.carless.driverapp.utils.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val session = SessionManager(this)
        val intent = when {
            !session.isLoggedIn() -> Intent(this, LoginActivity::class.java)
            session.isDriver() -> Intent(this, DriverHomeActivity::class.java)
            else -> Intent(this, PassengerHomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
