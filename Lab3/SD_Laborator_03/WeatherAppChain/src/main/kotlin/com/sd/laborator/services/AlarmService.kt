package com.sd.laborator.services

import com.sd.laborator.interfaces.AlarmInterface
import com.sd.laborator.pojo.WeatherForecastData
import com.sd.laborator.pojo.Weather_I_O
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStep
import io.github.damir.denis.tudor.spring.aop.chain.interfaces.Chainable
import org.springframework.stereotype.Service

@Service
@ChainStep
class AlarmService : AlarmInterface, Chainable<Weather_I_O, String> {
    override fun checkTemperature(tempCurenta: Int, pragInf : Int, pragSup : Int): Boolean {
        if(tempCurenta !in pragInf..pragSup)
            return true
        return false
    }

    override fun proceed(input: Weather_I_O): String {
        val forecastData = input.forecastData!!

        val triggerAlarm = checkTemperature(forecastData.currentTemp, 20, 30)
        if(triggerAlarm)
            return "Temperatura este inafara limitelor acceptate"

        return forecastData.toString()
    }
}