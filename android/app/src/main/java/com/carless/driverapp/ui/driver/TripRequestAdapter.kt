package com.carless.driverapp.ui.driver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carless.driverapp.data.model.TripResponse
import com.carless.driverapp.databinding.ItemTripRequestBinding

class TripRequestAdapter(
    private val onAccept: (TripResponse) -> Unit
) : ListAdapter<TripResponse, TripRequestAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTripRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: TripResponse) {
            binding.tvPassenger.text = trip.passenger?.name ?: "Passenger"
            binding.tvZone.text = "Zone: ${trip.zone}"
            binding.tvPickup.text = "From: ${trip.pickupAddress}"
            binding.tvDestination.text = "To: ${trip.destinationAddress}"
            binding.btnAccept.setOnClickListener { onAccept(trip) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTripRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<TripResponse>() {
        override fun areItemsTheSame(old: TripResponse, new: TripResponse) = old.id == new.id
        override fun areContentsTheSame(old: TripResponse, new: TripResponse) = old == new
    }
}