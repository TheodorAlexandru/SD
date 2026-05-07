package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import kotlin.random.Random

@EnableBinding(Source::class)
@SpringBootApplication
class ClientMicroservice {
    companion object {
        val listaProduse = java.io.File("/home/theo/Documents/SD/Lab9/SD_Laborator_09/exemplul 2/Fisiere_bd/produse.txt").readLines()

        val listaClienti = java.io.File("/home/theo/Documents/SD/Lab9/SD_Laborator_09/exemplul 2/Fisiere_bd/clienti.txt").readLines()
    }

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = [Poller(fixedDelay = "10000", maxMessagesPerPoll = "1")])
    fun comandaProdus(): () -> Message<String> {
        return {
            val produsComandat = listaProduse.random()
            val cantitate: Int = Random.nextInt(1, 100)
            val parti = listaClienti.random().split(",")
            val identitateClient = parti[0]
            val adresaLivrare = parti[1]

            val mesaj = "$identitateClient|$produsComandat|$cantitate|$adresaLivrare"
            MessageBuilder.withPayload(mesaj).build()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ClientMicroservice>(*args)
}