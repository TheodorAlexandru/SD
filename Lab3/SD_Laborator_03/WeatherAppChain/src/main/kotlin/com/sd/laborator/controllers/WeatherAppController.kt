package com.sd.laborator.controllers

import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.WeatherForecastData
import com.sd.laborator.pojo.Weather_I_O
import com.sd.laborator.services.AlarmService
import com.sd.laborator.services.BlackzoneService
import com.sd.laborator.services.LocationSearchService
import com.sd.laborator.services.TimeService
import com.sd.laborator.services.WeatherStartNodeService
import io.github.damir.denis.tudor.spring.aop.chain.registry.ChainResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAppController(
    private val weatherService : WeatherStartNodeService
) {
    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String{
        val result = weatherService.process(Weather_I_O(location))
        return result.result
    }
}