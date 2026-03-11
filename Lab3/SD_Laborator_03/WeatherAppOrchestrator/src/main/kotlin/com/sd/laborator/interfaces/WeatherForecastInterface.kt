package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface WeatherForecastInterface {
    fun getForecastData(locationName: String, coordonate: Pair<Double, Double>): WeatherForecastData
}