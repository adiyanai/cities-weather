package com.example.citiesweather.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
data class WeatherResponseDto(
    val temperature: Double,
    val humidity: Double?,
    val weatherCondition: String
)

