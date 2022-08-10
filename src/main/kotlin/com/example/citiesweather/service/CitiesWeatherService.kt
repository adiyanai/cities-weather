package com.example.citiesweather.service

import com.example.citiesweather.client.WeatherApiClient
import com.example.citiesweather.dto.TemperatureScaleType
import com.example.citiesweather.dto.WeatherResponseDto
import com.example.citiesweather.model.CurrentWeatherRequest
import com.example.citiesweather.model.WeatherRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class CitiesWeatherService(
    private val weatherApiClient: WeatherApiClient
) {

    suspend fun getHistoryWeather(
        weatherRequest: WeatherRequest
    ): WeatherResponseDto {
        val weatherApiResponse = weatherApiClient.getHistoryWeather(weatherRequest.city, weatherRequest.date)
        return weatherApiResponse.toWeatherResponseDto(weatherRequest.tempScaleType, weatherRequest.humidity)
    }

    suspend fun getFutureWeather(
        weatherRequest: WeatherRequest
    ): WeatherResponseDto {
        val weatherApiResponse = weatherApiClient.getFutureWeather(weatherRequest.city, weatherRequest.date)
        return weatherApiResponse.toWeatherResponseDto(weatherRequest.tempScaleType, weatherRequest.humidity)
    }

    suspend fun getCurrentWeather(
        weatherRequest: CurrentWeatherRequest
    ): WeatherResponseDto {
        val currentWeatherApiResponse = weatherApiClient.getCurrentWeather(weatherRequest.city)
        return currentWeatherApiResponse.toWeatherResponseDto(weatherRequest.tempScaleType, weatherRequest.humidity)
    }

    fun validateWeatherRequestDetails(req: ServerRequest): WeatherRequest {
        val errors = mutableListOf<String>()

        val city = req.queryParamOrNull("city") ?: null.also { errors.add("Query param city is missing") }

        val tempScaleType =
            when (val temp = req.queryParamOrNull("temperature_scale_type")) {
                null -> null.also { errors.add("Query param temperature_scale_type is missing") }
                else -> TemperatureScaleType.getByValue(temp.lowercase())
                    ?: null.also { errors.add("Illegal temperature scale type: $temp") }
            }

        val date = req.queryParamOrNull("date") ?: null.also { errors.add("Query param date is missing") }
        runCatching { date?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) } }.getOrElse {
            errors.add("Illegal date: $date")
        }

        val humidity =
            when (val h = req.queryParamOrNull("humidity")) {
                null -> null.also { errors.add("Query param humidity is missing") }
                "true", "false" -> h.toBoolean()
                else -> null.also { errors.add("Illegal humidity value: $h, should be true or false") }
            }

        if (errors.isNotEmpty()) {
            val errStr = errors.joinToString(".\n", "\n", ".")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse weather request: $errStr")
        } else return WeatherRequest(city!!, tempScaleType!!, date!!, humidity!!)
    }

    fun validateCurrentWeatherRequestDetails(req: ServerRequest): CurrentWeatherRequest {
        val errors = mutableListOf<String>()

        val city = req.queryParamOrNull("city") ?: null.also { errors.add("Query param city is missing") }

        val tempScaleType =
            when (val temp = req.queryParamOrNull("temperature_scale_type")) {
                null -> null.also { errors.add("Query param temperature_scale_type is missing") }
                else -> TemperatureScaleType.getByValue(temp.lowercase())
                    ?: null.also { errors.add("Illegal temperature scale type: $temp") }
            }

        val humidity =
            when (val h = req.queryParamOrNull("humidity")) {
                null -> null.also { errors.add("Query param humidity is missing") }
                "true", "false" -> h.toBoolean()
                else -> null.also { errors.add("Illegal humidity value: $h, should be true or false") }
            }

        if (errors.isNotEmpty()) {
            val errStr = errors.joinToString(".\n", "\n", ".")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse weather request: $errStr")
        } else return CurrentWeatherRequest(city!!, tempScaleType!!, humidity!!)
    }
}