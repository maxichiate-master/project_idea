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
import com.carless.driverapp.data.model.TripResponse
import com.carless.driverapp.databinding.ActivityDriverHomeBinding
import com.carless.driverapp.utils.Constants
import com.carless.driverapp.utils.SessionManager

class DriverHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverHomeBinding
    private val viewModel: DriverViewModel by viewModels()
    private lateinit var adapter: TripRequestAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var activeTripId: Long? = null

    private val pollRunnable = object : Runnable {
        override fun run() {
            if (activeTripId == null) viewModel.loadAvailableTrips()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvWelcome.text = "Hello, ${SessionManager(this).getName()}"

        val zoneAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.CABA_ZONES)
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerZone.adapter = zoneAdapter

        adapter = TripRequestAdapter { trip -> viewModel.acceptTrip(trip.id) }
        binding.rvTripRequests.layoutManager = LinearLayoutManager(this)
        binding.rvTripRequests.adapter = adapter

        binding.switchOnline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.spinnerZone.visibility = View.VISIBLE
                viewModel.setOnlineStatus(true, binding.spinnerZone.selectedItem.toString())
                handler.post(pollRunnable)
            } else {
                binding.spinnerZone.visibility = View.GONE
                viewModel.setOnlineStatus(false)
                handler.removeCallbacks(pollRunnable)
                adapter.submitList(emptyList())
            }
        }

        viewModel.availableTrips.observe(this) { trips ->
            adapter.submitList(trips)
            binding.tvNoTrips.visibility = if (trips.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.tripAction.observe(this) { result ->
            result.onSuccess { trip -> showActiveTripUI(trip) }
                .onFailure { Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show() }
        }

        binding.btnStartTrip.setOnClickListener { activeTripId?.let { viewModel.startTrip(it) } }
        binding.btnCompleteTrip.setOnClickListener { activeTripId?.let { viewModel.completeTrip(it) } }
    }

    private fun showActiveTripUI(trip: TripResponse) {
        activeTripId = trip.id
        binding.cardActiveTrip.visibility = View.VISIBLE
        binding.rvTripRequests.visibility = View.GONE
        binding.tvNoTrips.visibility = View.GONE
        binding.tvPassengerName.text = trip.passenger?.name ?: "Passenger"
        binding.tvPassengerPhone.text = trip.passenger?.phone ?: ""
        binding.tvPickupLabel.text = "Pickup: ${trip.pickupAddress}"
        binding.tvDestinationLabel.text = "To: ${trip.destinationAddress}"

        when (trip.status) {
            "ACCEPTED" -> {
                binding.btnStartTrip.visibility = View.VISIBLE
                binding.btnCompleteTrip.visibility = View.GONE
            }
            "IN_PROGRESS" -> {
                binding.btnStartTrip.visibility = View.GONE
                binding.btnCompleteTrip.visibility = View.VISIBLE
            }
            "COMPLETED" -> {
                activeTripId = null
                startActivity(Intent(this, RatingActivity::class.java).apply {
                    putExtra("tripId", trip.id)
                    putExtra("ratedName", trip.passenger?.name ?: "your passenger")
                })
                binding.cardActiveTrip.visibility = View.GONE
                binding.rvTripRequests.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable)
    }
}
