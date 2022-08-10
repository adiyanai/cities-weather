package com.example.citiesweather

import com.example.citiesweather.dto.WeatherResponseDto
import com.example.citiesweather.dto.toMapResponse
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.LocalDate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CitiesWeatherIntegrationTest(@Autowired val client: WebTestClient) {

    @Test
    fun `get history weather with humidity`() {
        val acceptedResult = toMapResponse(WeatherResponseDto(18.0, 78.0, "Light rain shower"))
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=celsius&date=2014-05-18&humidity=true")
                .exchange().expectStatus().isOk
                .expectBody(Map::class.java).isEqualTo(acceptedResult)
        }
    }

    @Test
    fun `get history weather without humidity`() {
        val acceptedResult = toMapResponse(WeatherResponseDto(18.0, null, "Light rain shower"))
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=celsius&date=2014-05-18&humidity=false")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk
                .expectBody(Map::class.java).isEqualTo(acceptedResult)
        }
    }

    @Test
    fun `get history weather with Fahrenheit temperature scale type`() {
        val acceptedResult = toMapResponse(WeatherResponseDto(64.4, 78.0, "Light rain shower"))
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=fahrenheit&date=2014-05-18&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk
                .expectBody(Map::class.java).isEqualTo(acceptedResult)
        }
    }

    @Test
    fun `get history weather without date query param - failed`() {
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=fahrenheit&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest
                .expectBody<String>()
                .consumeWith { Assertions.assertThat(it.responseBody).contains("Query param date is missing") }
        }
    }

    @Test
    fun `get history weather with illegal date - failed`() {
        val date = "2014-05-32"
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=fahrenheit&date=$date&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest
                .expectBody<String>()
                .consumeWith { Assertions.assertThat(it.responseBody).contains("Illegal date: $date") }

        }
    }

    @Test
    fun `get history weather with illegal temperature scale type - failed`() {
        val tempScaleType = "cel"
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=$tempScaleType&date=2014-05-18&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest
                .expectBody<String>()
                .consumeWith {
                    Assertions.assertThat(it.responseBody).contains("Illegal temperature scale type: $tempScaleType")
                }
        }
    }

    @Test
    fun `get history weather with too old date (before 2010-01-01) - failed`() {
        runBlocking {
            client.get()
                .uri("/api/weather/history?city=Milano&temperature_scale_type=celsius&date=2009-01-01&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest
        }
    }

    @Test
    fun `get future weather with humidity`() {
        val nextMonthDate = LocalDate.now().plusMonths(1).toString()
        runBlocking {
            client.get()
                .uri("/api/weather/future?city=paris&temperature_scale_type=CELSIUS&date=$nextMonthDate&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk
                .expectBody<Map<String, Any>>()
                .consumeWith { Assertions.assertThat(it.responseBody?.get("humidity")).isNotNull }
        }
    }

    @Test
    fun `get future weather without humidity`() {
        val nextMonthDate = LocalDate.now().plusMonths(1).toString()
        runBlocking {
            client.get()
                .uri("/api/weather/future?city=paris&temperature_scale_type=CELSIUS&date=$nextMonthDate&humidity=false")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk
                .expectBody<Map<String, Any>>()
                .consumeWith { Assertions.assertThat(it.responseBody?.get("humidity")).isNull() }
        }
    }

    @Test
    fun `get future with too close date (the date should be between 14 days and 300 days from today) - failed`() {
        val date = LocalDate.now().plusDays(10).toString()
        runBlocking {
            client.get()
                .uri("/api/weather/future?city=paris&temperature_scale_type=Fahrenheit&date=$date&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest
        }
    }

    @Test
    fun `get current weather with humidity`() {
        runBlocking {
            client.get()
                .uri("/api/weather/current?city=Tel Aviv&temperature_scale_type=celsius&humidity=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk
                .expectBody<Map<String, Any>>()
                .consumeWith { Assertions.assertThat(it.responseBody?.get("humidity")).isNotNull }
        }
    }

    @Test
    fun `get current weather without humidity`() {
        runBlocking {
            client.get()
                .uri("/api/weather/current?city=Tel Aviv&temperature_scale_type=Fahrenheit&humidity=false")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk
                .expectBody<Map<String, Any>>()
                .consumeWith { Assertions.assertThat(it.responseBody?.get("humidity")).isNull() }
        }

    }
}