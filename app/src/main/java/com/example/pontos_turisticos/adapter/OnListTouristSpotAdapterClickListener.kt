package com.example.pontos_turisticos.adapter

import com.example.pontos_turisticos.entidades.TouristSpot


interface OnListTouristSpotAdapterClickListener {
    fun onItemClick(touristSpot: TouristSpot)
}