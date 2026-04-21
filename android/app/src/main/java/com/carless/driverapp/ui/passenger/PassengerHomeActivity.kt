package com.carless.driverapp.ui.passenger

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.databinding.ActivityPassengerHomeBinding
import com.carless.driverapp.ui.driver.DriverUpgradeActivity
import com.carless.driverapp.utils.Constants
import com.carless.driverapp.utils.SessionManager

class PassengerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPassengerHomeBinding
    private val viewModel: PassengerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvWelcome.text = "Hello, ${SessionManager(this).getName()}"

        val zoneAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.CABA_ZONES)
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerZone.adapter = zoneAdapter

        binding.btnRequestDriver.setOnClickListener {
            val pickup = binding.etPickup.text.toString().trim()
            val destination = binding.etDestination.text.toString().trim()
            if (pickup.isEmpty() || destination.isEmpty()) {
                Toast.makeText(this, "Enter pickup and destination", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.requestTrip(pickup, destination, binding.spinnerZone.selectedItem.toString())
        }

        binding.btnBecomeDriver.setOnClickListener {
            startActivity(Intent(this, DriverUpgradeActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            SessionManager(this).logout()
            startActivity(Intent(this, com.carless.driverapp.ui.auth.LoginActivity::class.java))
            finishAffinity()
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnRequestDriver.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.tripCreated.observe(this) { result ->
            result.onSuccess { trip ->
                val intent = Intent(this, TripStatusActivity::class.java)
                intent.putExtra("tripId", trip.id)
                startActivity(intent)
            }.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
