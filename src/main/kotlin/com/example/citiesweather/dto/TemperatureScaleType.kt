package com.example.citiesweather.dto

enum class TemperatureScaleType(val value: String) {
    CELSIUS("celsius"),
    FAHRENHEIT("fahrenheit");

    companion object {
        fun getByValue(value: String): TemperatureScaleType? {
            return TemperatureScaleType.values().find {
                it.value == value
            }
        }
    }
}