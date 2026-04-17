package com.carless.driverapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.databinding.ActivityRegisterBinding
import com.carless.driverapp.ui.passenger.PassengerHomeActivity
import com.carless.driverapp.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.register(name, email, phone, password)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnRegister.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.authResult.observe(this) { result ->
            result.onSuccess { auth ->
                SessionManager(this).saveAuthData(auth.token, auth.userId, auth.name, auth.driver)
                startActivity(Intent(this, PassengerHomeActivity::class.java))
                finishAffinity()
            }.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
