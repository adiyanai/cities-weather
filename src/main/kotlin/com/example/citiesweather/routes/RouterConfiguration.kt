package com.example.citiesweather.routes

import com.example.citiesweather.controllers.CitiesWeatherController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter


@Configuration
class RouterConfiguration(
    private val weatherController: CitiesWeatherController
) {

    @Bean
    fun appRoutes(weatherController: CitiesWeatherController) = coRouter {
        "/api/weather".nest {
            GET("/history", weatherController::getHistoryWeather)
            GET("/future", weatherController::getFutureWeather)
            GET("/current", weatherController::getCurrentWeather)
        }
    }
}
