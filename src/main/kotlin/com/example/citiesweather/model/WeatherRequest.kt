package com.example.citiesweather.model

import com.example.citiesweather.dto.TemperatureScaleType

data class WeatherRequest (
    val city: String,
    val tempScaleType: TemperatureScaleType,
    val date: String,
    val humidity: Boolean
)

data class CurrentWeatherRequest (
    val city: String,
    val tempScaleType: TemperatureScaleType,
    val humidity: Boolean
)