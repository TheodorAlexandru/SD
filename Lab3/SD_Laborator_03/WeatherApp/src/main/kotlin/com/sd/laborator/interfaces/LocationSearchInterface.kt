package com.sd.laborator.interfaces

interface LocationSearchInterface {
    fun getLatAndLong(locationName: String): Pair<Double, Double>?
}