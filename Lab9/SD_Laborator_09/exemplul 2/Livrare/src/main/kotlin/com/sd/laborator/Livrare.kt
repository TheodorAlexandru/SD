package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
@SpringBootApplication
class LivrareMicroservice {
    @StreamListener(Sink.INPUT)
    ///TODO - parametrul ar trebui sa fie doar numarul de inregistrare al comenzii si atat
    fun expediereComanda(comandaSiStatus: String) {
        val (idComanda, status) = comandaSiStatus!!.split("|")
        if (status == "ACCEPTATA")
            println("Comanda $idComanda a fost livrata destinatarului")
        else
            println("Nu a fost stoc suficient pentru a livra comanda $idComanda")
    }
}

fun main(args: Array<String>) {
    runApplication<LivrareMicroservice>(*args)
}