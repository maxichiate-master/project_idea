package com.carless.driverapp.ui.passenger

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.databinding.ActivityTripStatusBinding
import com.carless.driverapp.ui.driver.RatingActivity

class TripStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripStatusBinding
    private val viewModel: PassengerViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var tripId: Long = -1

    private val pollRunnable = object : Runnable {
        override fun run() {
            viewModel.pollActiveTrip()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getLongExtra("tripId", -1)

        binding.btnCancel.setOnClickListener {
            viewModel.cancelTrip(tripId)
            finish()
        }

        viewModel.activeTrip.observe(this) { trip ->
            if (trip == null) return@observe
            updateUI(trip)
            when (trip.status) {
                "COMPLETED" -> {
                    handler.removeCallbacks(pollRunnable)
                    startActivity(Intent(this, RatingActivity::class.java).apply {
                        putExtra("tripId", trip.id)
                        putExtra("ratedName", trip.driver?.name ?: "your driver")
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

        handler.post(pollRunnable)
    }

    private fun updateUI(trip: com.carless.driverapp.data.model.TripResponse) {
        binding.tvStatus.text = when (trip.status) {
            "REQUESTED" -> "Looking for a driver..."
            "ACCEPTED" -> "Driver is on the way!"
            "IN_PROGRESS" -> "Trip in progress"
            else -> trip.status
        }
        if (trip.driver != null) {
            binding.cardDriverInfo.visibility = View.VISIBLE
            binding.tvDriverName.text = trip.driver.name
            binding.tvDriverPhone.text = trip.driver.phone
            binding.btnCancel.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable)
    }
}
