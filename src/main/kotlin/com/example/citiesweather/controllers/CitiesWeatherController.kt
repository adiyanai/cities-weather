package com.example.citiesweather.controllers

import com.example.citiesweather.service.CitiesWeatherService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*


@RestController
class CitiesWeatherController(
    private val citiesWeatherService: CitiesWeatherService
) {

    suspend fun getHistoryWeather(req: ServerRequest): ServerResponse {
        val validatedRequest = citiesWeatherService.validateWeatherRequestDetails(req)
        val response = citiesWeatherService.getHistoryWeather(validatedRequest)
        return ServerResponse.ok().json().bodyValueAndAwait(response)
    }

    suspend fun getFutureWeather(req: ServerRequest): ServerResponse {
        val validatedRequest = citiesWeatherService.validateWeatherRequestDetails(req)
        val response = citiesWeatherService.getFutureWeather(validatedRequest)
        return ServerResponse.ok().json().bodyValueAndAwait(response)
    }

    suspend fun getCurrentWeather(req: ServerRequest): ServerResponse {
        val validatedRequest = citiesWeatherService.validateCurrentWeatherRequestDetails(req)
        val response = citiesWeatherService.getCurrentWeather(validatedRequest)
        return ServerResponse.ok().json().bodyValueAndAwait(response)
    }
}
