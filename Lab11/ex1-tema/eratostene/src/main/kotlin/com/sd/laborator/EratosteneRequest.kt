package com.sd.laborator
import io.micronaut.core.annotation.Introspected
@Introspected
class EratosteneRequest {
    private lateinit var number: Integer
    private lateinit var numbersToCheck: List<Int>

    fun getNumber(): Int {
        return number.toInt()
    }

    fun getNumbersToCheck(): List<Int> {
        return numbersToCheck
    }
}