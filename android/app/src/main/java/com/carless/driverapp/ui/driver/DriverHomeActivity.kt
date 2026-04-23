package com.carless.driverapp.ui.driver

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.carless.driverapp.databinding.ActivityDriverHomeBinding
import com.carless.driverapp.ui.auth.LoginActivity
import com.carless.driverapp.ui.trip.ActiveTripActivity
import com.carless.driverapp.utils.Constants
import com.carless.driverapp.utils.SessionManager

class DriverHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverHomeBinding
    private val viewModel: DriverViewModel by viewModels()
    private lateinit var adapter: TripRequestAdapter
    private val handler = Handler(Looper.getMainLooper())

    private val pollRunnable = object : Runnable {
        override fun run() {
            viewModel.loadAvailableTrips()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        binding.tvWelcome.text = "Hello, ${session.getName()}"

        binding.btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        val zoneAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.CABA_ZONES)
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerZone.adapter = zoneAdapter
        binding.spinnerZone.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                if (binding.switchOnline.isChecked) {
                    val zone = parent.getItemAtPosition(pos).toString()
                    adapter.submitList(emptyList())
                    binding.tvNoTrips.visibility = View.GONE
                    handler.removeCallbacks(pollRunnable)
                    handler.postDelayed(pollRunnable, 5000)
                    viewModel.goOnline(zone)
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) = Unit
        }

        adapter = TripRequestAdapter { trip -> viewModel.acceptTrip(trip.id) }
        binding.rvTripRequests.layoutManager = LinearLayoutManager(this)
        binding.rvTripRequests.adapter = adapter

        binding.switchOnline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.spinnerZone.visibility = View.VISIBLE
                viewModel.goOnline(binding.spinnerZone.selectedItem.toString())
                handler.postDelayed(pollRunnable, 5000)
            } else {
                binding.spinnerZone.visibility = View.GONE
                viewModel.setOffline()
                handler.removeCallbacks(pollRunnable)
                adapter.submitList(emptyList())
                binding.tvNoTrips.visibility = View.GONE
            }
        }

        viewModel.availableTrips.observe(this) { trips ->
            adapter.submitList(trips)
            binding.tvNoTrips.visibility = if (trips.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.tripAction.observe(this) { result ->
            result.onSuccess { trip ->
                handler.removeCallbacks(pollRunnable)
                startActivity(Intent(this, ActiveTripActivity::class.java).apply {
                    putExtra("tripId", trip.id)
                    putExtra("isDriver", true)
                })
            }.onFailure {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        // Resume an active trip if the driver restarts the app mid-trip
        viewModel.activeTrip.observe(this) { trip ->
            if (trip != null && trip.status in listOf("ACCEPTED", "IN_PROGRESS")) {
                startActivity(Intent(this, ActiveTripActivity::class.java).apply {
                    putExtra("tripId", trip.id)
                    putExtra("isDriver", true)
                })
            }
        }
        viewModel.checkActiveTrip()
    }

    override fun onResume() {
        super.onResume()
        if (binding.switchOnline.isChecked) {
            handler.removeCallbacks(pollRunnable)
            handler.post(pollRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable)
    }
}