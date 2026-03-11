package com.sd.laborator.services

import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.pojo.Weather_I_O
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStep
import io.github.damir.denis.tudor.spring.aop.chain.interfaces.Chainable
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
@ChainStep(next = WeatherForecastService::class)
class LocationSearchService : LocationSearchInterface, Chainable<Weather_I_O, Weather_I_O> {
    override fun getLatAndLong(locationName: String): Pair<Double, Double>?{
        // codificare parametru URL (deoarece poate conţine caractere speciale)
        val encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8.toString())

        // construire obiect de tip URL
        val locationSearchURL = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedLocationName&count=1")

        // preluare raspuns HTTP (se face cerere GET şi se preia conţinutul răspunsului sub formă de text)
        val rawResponse: String = locationSearchURL.readText()

        val root = JSONObject(rawResponse)

        if(!root.has("results"))
        {
            return null
        }

        val result = root.getJSONArray("results").getJSONObject(0)

        return Pair(result.getDouble("latitude"), result.getDouble("longitude"))

    }

    override fun proceed(input: Weather_I_O): Weather_I_O {
        val coord = getLatAndLong(input.locationName) ?: throw RuntimeException("Nu s-au putut gasi coordonatele pentru ${input.locationName}")

        input.coordonate = coord

        return input
    }
}