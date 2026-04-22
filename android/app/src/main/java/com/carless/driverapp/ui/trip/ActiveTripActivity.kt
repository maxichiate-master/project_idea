package com.carless.driverapp.ui.trip

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.data.model.TripResponse
import com.carless.driverapp.databinding.ActivityActiveTripBinding
import com.carless.driverapp.ui.driver.RatingActivity

class ActiveTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveTripBinding
    private val viewModel: TripViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var tripId: Long = -1
    private var isDriver: Boolean = false

    private val pollRunnable = object : Runnable {
        override fun run() {
            viewModel.pollTrip(tripId)
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getLongExtra("tripId", -1)
        isDriver = intent.getBooleanExtra("isDriver", false)

        binding.btnStartTrip.setOnClickListener { viewModel.startTrip(tripId) }
        binding.btnCompleteTrip.setOnClickListener { viewModel.completeTrip(tripId) }
        binding.btnCancel.setOnClickListener { viewModel.cancelTrip(tripId) }

        viewModel.trip.observe(this) { trip ->
            if (trip == null) return@observe
            updateUI(trip)
        }

        viewModel.actionResult.observe(this) { result ->
            result.onSuccess { trip -> updateUI(trip) }
                .onFailure { Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show() }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnStartTrip.isEnabled = !loading
            binding.btnCompleteTrip.isEnabled = !loading
            binding.btnCancel.isEnabled = !loading
        }

        handler.post(pollRunnable)
    }

    private fun updateUI(trip: TripResponse) {
        binding.tvZone.text = "Zone: ${trip.zone}"
        binding.tvPickup.text = "From: ${trip.pickupAddress}"
        binding.tvDestination.text = "To: ${trip.destinationAddress}"

        binding.tvStatus.text = when (trip.status) {
            "REQUESTED" -> if (isDriver) "New Trip" else "Looking for a driver..."
            "ACCEPTED"  -> if (isDriver) "Heading to passenger" else "Driver is on the way!"
            "IN_PROGRESS" -> "Trip in progress"
            "COMPLETED"   -> "Trip completed"
            "CANCELLED"   -> "Trip cancelled"
            else -> trip.status
        }

        binding.tvStatusSubtitle.text = when (trip.status) {
            "ACCEPTED"    -> if (isDriver) "Head to the pickup address" else "Your driver is coming"
            "IN_PROGRESS" -> if (isDriver) "Drive safely!" else "Enjoy your ride!"
            else -> ""
        }

        val otherParty = if (isDriver) trip.passenger else trip.driver
        if (otherParty != null) {
            binding.cardOtherParty.visibility = View.VISIBLE
            binding.tvOtherPartyLabel.text = if (isDriver) "PASSENGER" else "YOUR DRIVER"
            binding.tvOtherPartyName.text = otherParty.name
            binding.tvOtherPartyPhone.text = otherParty.phone
        }

        if (isDriver) {
            binding.btnStartTrip.visibility   = if (trip.status == "ACCEPTED") View.VISIBLE else View.GONE
            binding.btnCompleteTrip.visibility = if (trip.status == "IN_PROGRESS") View.VISIBLE else View.GONE
            binding.btnCancel.visibility       = if (trip.status == "ACCEPTED") View.VISIBLE else View.GONE
        } else {
            binding.btnStartTrip.visibility   = View.GONE
            binding.btnCompleteTrip.visibility = View.GONE
            binding.btnCancel.visibility = if (trip.status in listOf("REQUESTED", "ACCEPTED")) View.VISIBLE else View.GONE
        }

        when (trip.status) {
            "COMPLETED" -> {
                handler.removeCallbacks(pollRunnable)
                val ratedName = if (isDriver) trip.passenger?.name ?: "your passenger"
                               else trip.driver?.name ?: "your driver"
                startActivity(Intent(this, RatingActivity::class.java).apply {
                    putExtra("tripId", trip.id)
                    putExtra("ratedName", ratedName)
                })
                finish()
            }
            "CANCELLED" -> {
                handler.removeCallbacks(pollRunnable)
                Toast.makeText(this, "Trip was cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable)
    }
}