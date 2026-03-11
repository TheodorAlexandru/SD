package com.sd.laborator.services

import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service
class OrchestratorService (
    private val blackzone : BlackzoneService,
    private val locationSearch : LocationSearchService,
    private val forecastData : WeatherForecastService,
    private val alarm : AlarmService
)
{
    fun getForecastData(locationName : String) : String
    {
        if(blackzone.isServerLocationBlacklisted())
        {
            val zone = blackzone.getServerLocation()
            return "Zona $zone se afla in blacklist."
        }

        val coord = locationSearch.getLatAndLong(locationName) ?: throw RuntimeException("Nu s-a putut gasi locatia")

        val data = forecastData.getForecastData(locationName, coord)

        val alarmTrigger = alarm.checkTemperature(data.currentTemp, 5, 30)
        if(alarmTrigger)
            return "Temperatura nu este in limitele permise."

        return data.toString()

    }
}