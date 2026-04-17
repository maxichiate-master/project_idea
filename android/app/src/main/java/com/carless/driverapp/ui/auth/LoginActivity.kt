package com.carless.driverapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.databinding.ActivityLoginBinding
import com.carless.driverapp.ui.driver.DriverHomeActivity
import com.carless.driverapp.ui.passenger.PassengerHomeActivity
import com.carless.driverapp.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnLogin.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.authResult.observe(this) { result ->
            result.onSuccess { auth ->
                SessionManager(this).saveAuthData(auth.token, auth.userId, auth.name, auth.driver)
                val dest = if (auth.driver) DriverHomeActivity::class.java else PassengerHomeActivity::class.java
                startActivity(Intent(this, dest))
                finishAffinity()
            }.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
