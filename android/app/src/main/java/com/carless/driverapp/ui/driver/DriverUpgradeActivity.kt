package com.carless.driverapp.ui.driver

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.databinding.ActivityDriverUpgradeBinding

class DriverUpgradeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverUpgradeBinding
    private val viewModel: DriverViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverUpgradeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val dni = binding.etDni.text.toString().trim()
            val license = binding.etLicense.text.toString().trim()
            if (dni.isEmpty() || license.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.applyForDriver(dni, license)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnSubmit.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.driverProfile.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Application submitted! We'll review it soon.", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
