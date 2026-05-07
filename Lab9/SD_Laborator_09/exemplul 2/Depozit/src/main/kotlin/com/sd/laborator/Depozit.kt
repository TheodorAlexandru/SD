package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import org.springframework.messaging.support.MessageBuilder
import kotlin.random.Random

@EnableBinding(Processor::class)
@SpringBootApplication
class DepozitMicroservice {
    companion object {
        val fisierStocProduse = java.io.File("/home/theo/Documents/SD/Lab9/SD_Laborator_09/exemplul 2/Fisiere_bd/stoc_produse.txt")
        val fisierComenzi = java.io.File("/home/theo/Documents/SD/Lab9/SD_Laborator_09/exemplul 2/Fisiere_bd/comenzi.txt")
    }

    private fun acceptareComanda(identificator: String, produs: String, cantitate: Int): String {
        pregatireColet(produs, cantitate)
        return "ACCEPTATA"
    }

    private fun respingereComanda(identificator: String): String {
        println("Comanda cu identificatorul $identificator a fost respinsa! Stoc insuficient.")
        return "RESPINSA"
    }

    private fun verificareStoc(produs: String, cantitate: Int): Boolean {
        val stocProduse = fisierStocProduse.readLines()
        for(linie in stocProduse)
        {
            val parti = linie.split("|")
            if(parti[0] == produs && parti[1].toInt() >= cantitate)
                return true
        }
        return false
    }

    private fun pregatireColet(produs: String, cantitate: Int){
        println("Produsul $produs in cantitate de $cantitate buc. este pregatit de livrare.")

        val liniiStoc = fisierStocProduse.readLines()

        val stocActualizat = liniiStoc.map { linie ->
            val parti = linie.split("|")

            if(parti[0] == produs) {
                val stocVechi = parti[1].toInt()
                val stocNou = stocVechi - cantitate
                "${parti[0]}|$stocNou"
            }
            else {
                linie
            }
        }

        fisierStocProduse.writeText(stocActualizat.joinToString("\n"))
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun procesareComanda(comanda: String): String {
        println("Procesez comanda cu identificatorul $comanda")

        val preluareComanda = fisierComenzi.useLines { it.last() }
        val parti = preluareComanda.substringAfter("|").split("|")
        val produs = parti[0].replace("Produs:", "").trim()
        val cantitate = parti[1].replace("Cantitate:", "").trim().toInt()

        val rezultatProcesareComanda: String = if (verificareStoc(produs, cantitate)) {
            acceptareComanda(comanda, produs, cantitate)
        } else {
            respingereComanda(comanda)
        }

        ///TODO - in loc sa se trimita mesajul cu toate datele in continuare, trebuie trimis doar ID-ul comenzii
        return "$comanda|$rezultatProcesareComanda"
    }
}

fun main(args: Array<String>) {
    runApplication<DepozitMicroservice>(*args)
}