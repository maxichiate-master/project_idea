package com.carless.driverapp.ui.driver

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carless.driverapp.data.api.ApiClient
import com.carless.driverapp.data.model.RatingRequest
import com.carless.driverapp.databinding.ActivityRatingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getLongExtra("tripId", -1)
        val ratedName = intent.getStringExtra("ratedName") ?: "your driver"
        binding.tvRatingPrompt.text = "How was your experience with $ratedName?"

        binding.btnSubmitRating.setOnClickListener {
            val score = binding.ratingBar.rating.toInt()
            if (score == 0) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val comment = binding.etComment.text.toString().trim().ifEmpty { null }
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    ApiClient.getService().rateTrip(RatingRequest(tripId, score, comment))
                } catch (e: Exception) { /* rating is optional */ }
                finish()
            }
        }

        binding.tvSkip.setOnClickListener { finish() }
    }
}
