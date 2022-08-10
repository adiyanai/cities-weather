package com.example.citiesweather.dto

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

data class WeatherResponseDto(
    val temperature: Double,
    val humidity: Double?,
    val weatherCondition: String
)

fun toMapResponse(weatherResponseDto: WeatherResponseDto): Map<*, *> {
    val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        .setSerializationInclusion(Include.NON_NULL)
    return mapper.convertValue(weatherResponseDto, Map::class.java)
}

