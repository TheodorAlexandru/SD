package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneQueueRequest {
    private lateinit var numbersToCheck: List<Int>

    fun getNumbersToCheck(): List<Int> {
        return numbersToCheck
    }
}