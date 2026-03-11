package com.sd.laborator.pojo

import com.sd.laborator.pojo.WeatherForecastData

data class Weather_I_O(
    var locationName : String,
    var coordonate: Pair<Double, Double>? = null,
    var forecastData: WeatherForecastData? = null,
    var response: String? = null
)