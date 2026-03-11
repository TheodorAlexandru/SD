package com.sd.laborator.services

import com.sd.laborator.interfaces.BlackzoneInterface
import com.sd.laborator.pojo.Weather_I_O
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStep
import io.github.damir.denis.tudor.spring.aop.chain.interfaces.Chainable
import org.springframework.stereotype.Service
import java.io.File
import java.util.TimeZone

@Service
@ChainStep(next = LocationSearchService::class)
class BlackzoneService : BlackzoneInterface, Chainable<Weather_I_O, Weather_I_O> {

    override fun getServerLocation(): String
    {
        return TimeZone.getDefault().id
    }

    override fun isServerLocationBlacklisted(): Boolean
    {
        val currentZone = getServerLocation()

        val textFisier = File("blacklist.txt").readText()

        if(textFisier.contains(currentZone))
            return true
        return false
    }

    override fun proceed(input: Weather_I_O): Weather_I_O {
        if(isServerLocationBlacklisted()) {
            val zone = getServerLocation()
            throw RuntimeException("Nu este permis accesul la informatii pentru zona $zone")
        }
        return input
    }
}