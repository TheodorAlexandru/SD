package com.sd.laborator.services

import com.sd.laborator.interfaces.BlackzoneInterface
import org.springframework.stereotype.Service
import java.io.File
import java.util.TimeZone

@Service
class BlackzoneService : BlackzoneInterface {

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
}