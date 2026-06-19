package com.sd.laborator

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject

@Controller("/preiaNumere")
class PreiaNumereController {

    @Inject
    lateinit var clientCoada: NumerePrimeClient

    @Get(uri="/", produces=["application/json"])
    fun index(){
        val numere = javaClass.getResource("/numere.txt")!!.readText().trim()
        val creazaListaNumere = numere.split(" ").map{ it.toInt() }
        clientCoada.trimiteCatreCoada(mapOf("numbersToCheck" to creazaListaNumere))

    }
}