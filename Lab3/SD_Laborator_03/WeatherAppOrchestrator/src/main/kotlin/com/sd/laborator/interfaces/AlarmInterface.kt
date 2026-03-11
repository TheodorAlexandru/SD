package com.sd.laborator.interfaces

interface AlarmInterface {
    fun checkTemperature(tempCurenta : Int, pragInf : Int, pragSup : Int) : Boolean
}