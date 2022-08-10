package com.example.citiesweather.client

import com.example.citiesweather.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.server.ResponseStatusException
import java.net.URLEncoder

@Component
class WeatherApiClient(
    @Value("\${weather.apiKey}")
    private val apiKey: String,
    @Value("\${weather.historyUrl}")
    private val historyUrl: String,
    @Value("\${weather.currentUrl}")
    private val currentUrl: String,
    @Value("\${weather.futureUrl}")
    private val futureUrl: String,
    private val webClient: WebClient
) {

    suspend fun getHistoryWeather(city: String, date: String): WeatherApiResponse {
        val url = generateUrl(historyUrl, city, date)
        return callWeatherApi(url)
    }

    suspend fun getFutureWeather(city: String, date: String): WeatherApiResponse {
        val url = generateUrl(futureUrl, city, date)
        return callWeatherApi(url)
    }

    suspend fun getCurrentWeather(city: String): CurrentWeatherApiResponse = coroutineScope {
        val url = generateUrl(currentUrl, city)
        runCatching { webClient.get().uri(url).retrieve().awaitBody<CurrentWeatherApiResponse>() }.getOrElse {
            when (it) {
                is BadRequest -> throw ResponseStatusException(
                    it.statusCode,
                    mapToWeatherApiErrorResponse(it.responseBodyAsString).error.message
                )
                else -> throw InternalError()
            }
        }
    }

    suspend fun callWeatherApi(url: String): WeatherApiResponse = coroutineScope {
        runCatching { webClient.get().uri(url).retrieve().awaitBody<WeatherApiResponse>() }.getOrElse {
            when (it) {
                is BadRequest -> throw ResponseStatusException(
                    it.statusCode,
                    mapToWeatherApiErrorResponse(it.responseBodyAsString).error.message
                )
                else -> throw InternalError()
            }
        }
    }

    fun generateUrl(baseUrl: String, city: String, date: String? = null): String {
        val url = "$baseUrl?key=$apiKey&q=${URLEncoder.encode(city, "UTF-8")}"
        return if (date == null) url else "$url&dt=$date"
    }

    fun mapToWeatherApiErrorResponse(response: String): WeatherApiErrorResponse {
        return ObjectMapper().readValue(response, WeatherApiErrorResponse::class.java)
    }
}