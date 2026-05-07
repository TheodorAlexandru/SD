package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import org.springframework.messaging.support.MessageBuilder
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.random.Random

@EnableBinding(Processor::class)
@SpringBootApplication
class ComandaMicroservice {
    private fun pregatireComanda(produs: String, cantitate: Int): Int {
        println("Se pregateste comanda $cantitate x \"$produs\"...")

        val fisierComenzi = java.io.File("/home/theo/Documents/SD/Lab9/SD_Laborator_09/exemplul 2/Fisiere_bd/comenzi.txt")
        val ultimaLinie = fisierComenzi.useLines { it.lastOrNull() }
        val idAnterior = if (ultimaLinie.isNullOrBlank()) {
            0
        } else {
            ultimaLinie.substringBefore("|").replace("ID:", "").trim().toIntOrNull() ?: 0
        }
        val nrComanda = idAnterior + 1
        fisierComenzi.appendText("ID: $nrComanda|Produs: $produs|Cantitate: $cantitate\r\n")
        return nrComanda
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun preluareComanda(comanda: String?): String {
        val (identitateClient, produsComandat, cantitate, adresaLivrare) = comanda!!.split("|")
        println("Am primit comanda urmatoare: ")
        println("$identitateClient | $produsComandat | $cantitate | $adresaLivrare")

        val idComanda = pregatireComanda(produsComandat, cantitate.toInt())

        return "$idComanda"
    }
}

fun main(args: Array<String>) {
    runApplication<ComandaMicroservice>(*args)
}