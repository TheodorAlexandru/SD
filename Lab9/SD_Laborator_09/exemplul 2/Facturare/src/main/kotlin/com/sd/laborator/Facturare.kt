package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextUInt

@EnableBinding(Processor::class)
@SpringBootApplication
class FacturareMicroservice {
    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun emitereFactura(comandaSiStatus: String?): String {
        val (idComanda, status) = comandaSiStatus!!.split("|")
        if(status == "ACCEPTATA") {
            println("Emit factura pentru comanda $idComanda...")
            val nrFactura = abs(Random.nextInt())

            val fisierFacturi = java.io.File("/home/theo/Documents/SD/Lab9/SD_Laborator_09/exemplul 2/Fisiere_bd/facturi.txt")

            if(!fisierFacturi.exists()) {
                fisierFacturi.createNewFile()
            }

            fisierFacturi.appendText("ID comanda: $idComanda|NR factura: $nrFactura\r\n")
        }
        else{
            println("Comanda ID: $idComanda este RESPINSA. Nu se emite nicio factură.")
        }
        return comandaSiStatus
    }
}

fun main(args: Array<String>) {
    runApplication<FacturareMicroservice>(*args)
}