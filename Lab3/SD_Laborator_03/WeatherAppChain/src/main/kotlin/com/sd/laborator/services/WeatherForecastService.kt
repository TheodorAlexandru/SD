package com.sd.laborator.services

import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.WeatherForecastData
import com.sd.laborator.pojo.Weather_I_O
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStep
import io.github.damir.denis.tudor.spring.aop.chain.interfaces.Chainable
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import kotlin.math.roundToInt

@Service
@ChainStep(next = AlarmService::class)
class WeatherForecastService (private val timeService: TimeService) : WeatherForecastInterface, Chainable<Weather_I_O, Weather_I_O> {
    override fun getForecastData(locationName: String, coordonate: Pair<Double, Double>): WeatherForecastData {
        val latitude = coordonate.first
        val longitude = coordonate.second

        // ID-ul locaţiei nu trebuie codificat, deoarece este numeric
        val forecastDataURL = URL("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m&daily=temperature_2m_max,temperature_2m_min&timezone=auto")

        // preluare conţinut răspuns HTTP la o cerere GET către URL-ul de mai sus
        val rawResponse: String = forecastDataURL.readText()

        // parsare obiect JSON primit
        val responseRootObject = JSONObject(rawResponse)
        val current = responseRootObject.getJSONObject("current")
        val daily = responseRootObject.getJSONObject("daily")

        // construire şi returnare obiect POJO care încapsulează datele meteo
        return WeatherForecastData(
            location = locationName,
            date = timeService.getCurrentTime(),
            weatherState = "Weather State: ${current.getInt("weather_code")}",
            windDirection = "Directia vantului: ${current.getInt("wind_direction_10m")} grade",
            windSpeed = current.getDouble("wind_speed_10m").roundToInt(),
            minTemp = daily.getJSONArray("temperature_2m_min").getDouble(0).roundToInt(),
            maxTemp = daily.getJSONArray("temperature_2m_max").getDouble(0).roundToInt(),
            currentTemp = current.getDouble("temperature_2m").roundToInt(),
            humidity = current.getInt("relative_humidity_2m")
        )
    }

    override fun proceed(input: Weather_I_O): Weather_I_O {
        val forcastData = getForecastData(input.locationName, input.coordonate!!)
        input.forecastData = forcastData
        return input
    }
}