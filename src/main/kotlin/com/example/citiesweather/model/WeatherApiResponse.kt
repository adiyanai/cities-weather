package com.example.citiesweather.model

import com.example.citiesweather.dto.TemperatureScaleType
import com.fasterxml.jackson.annotation.JsonProperty
import com.example.citiesweather.dto.TemperatureScaleType.*
import com.example.citiesweather.dto.WeatherResponseDto
import org.springframework.cache.annotation.Cacheable

// History/Future Weather API response
data class WeatherApiResponse(
    @JsonProperty("forecast")
    val forecast: Forecast
) {
    fun toWeatherResponseDto(tempScaleType: TemperatureScaleType, humidity: Boolean): WeatherResponseDto {
        val avgTemp = getTemperature(tempScaleType)
        val avgHumidity = getHumidity(humidity)
        val weatherCondition = getWeatherCondition()
        return WeatherResponseDto(avgTemp, avgHumidity, weatherCondition)
    }

    private fun getTemperature(tempScaleType: TemperatureScaleType): Double {
        return when (tempScaleType) {
            CELSIUS -> forecast.forecastDay.first().day.avgTempC
            FAHRENHEIT -> forecast.forecastDay.first().day.avgTempF
        }
    }

    private fun getHumidity(humidity: Boolean): Double? =
        if (humidity) forecast.forecastDay.first().day.avgHumidity else null

    private fun getWeatherCondition(): String = forecast.forecastDay.first().day.condition.text
}

data class Forecast(
    @JsonProperty("forecastday")
    val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    @JsonProperty("day")
    val day: Day
)

data class Day(
    @JsonProperty("avgtemp_c")
    val avgTempC: Double,
    @JsonProperty("avgtemp_f")
    val avgTempF: Double,
    @JsonProperty("avghumidity")
    val avgHumidity: Double,
    @JsonProperty("condition")
    val condition: Condition
)

data class Condition(
    @JsonProperty("text")
    val text: String
)


// Current Weather API response
data class CurrentWeatherApiResponse(
    @JsonProperty("current")
    val current: Current
) {
    fun toWeatherResponseDto(tempScaleType: TemperatureScaleType, humidity: Boolean): WeatherResponseDto {
        val avgTemp = getTemperature(tempScaleType)
        val avgHumidity = getHumidity(humidity)
        val weatherCondition = getWeatherCondition()
        return WeatherResponseDto(avgTemp, avgHumidity, weatherCondition)
    }

    private fun getTemperature(tempScaleType: TemperatureScaleType): Double {
        return when (tempScaleType) {
            CELSIUS -> current.tempC
            FAHRENHEIT -> current.tempF
        }
    }

    private fun getHumidity(humidity: Boolean): Double? = if (humidity) current.humidity else null
    private fun getWeatherCondition(): String = current.condition.text
}

data class Current(
    @JsonProperty("temp_c")
    val tempC: Double,
    @JsonProperty("temp_f")
    val tempF: Double,
    @JsonProperty("humidity")
    val humidity: Double,
    @JsonProperty("condition")
    val condition: Condition
)

// Weather Api Failure response
data class WeatherApiErrorResponse(
    @JsonProperty("error")
    val error: Error
)

data class Error(
    @JsonProperty("code")
    val code: String,
    @JsonProperty("message")
    val message: String
)