package com.example.citiesweather

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class AppConfig {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient? {
        return builder.build()
    }
}