package com.example.pontos_turisticos.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pontos_turisticos.databinding.CardTouristSpotBinding
import com.example.pontos_turisticos.entidades.TouristSpot

class ListTouristSpotAdpter (
    private val context: Context,
    touristSpots: List<TouristSpot>,
    private val listener: OnListTouristSpotAdapterClickListener
) : RecyclerView.Adapter<ListTouristSpotAdpter.ViewHolder>(){
    private val touristSpots = touristSpots.toMutableList()

    class ViewHolder(binding: CardTouristSpotBinding) : RecyclerView.ViewHolder(binding.root) {
        private val name = binding.tvSpotName
        private val description = binding.tvSpotDescription
        private val latitude = binding.tvSpotLatitude
        private val longitude = binding.tvSpotLongitude
        //private val spotPhoto = binding.ivSpotImage

        fun bind(touristSpot: TouristSpot) {
            name.text = touristSpot.name
            description.text = touristSpot.description
            latitude.text = touristSpot.latitude
            longitude.text = touristSpot.longitude
            // val bitmap = BitmapFactory.decodeByteArray(touristSpot.spotImage, 0, touristSpot.spotImage.size)
            //spotPhoto.setImageBitmap(bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardTouristSpotBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = touristSpots.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pet = touristSpots[position]
        holder.bind(pet)
        holder.itemView.setOnClickListener { listener.onItemClick(pet) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(touristSpots: List<TouristSpot>) {
        this.touristSpots.clear()
        this.touristSpots.addAll(touristSpots)
        
        notifyDataSetChanged()
    }
}