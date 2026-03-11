package com.sd.laborator.services

import com.sd.laborator.interfaces.AlarmInterface
import org.springframework.stereotype.Service

@Service
class AlarmService : AlarmInterface {
    override fun checkTemperature(tempCurenta: Int, pragInf : Int, pragSup : Int): Boolean {
        if(tempCurenta !in pragInf..pragSup)
            return true
        return false
    }
}