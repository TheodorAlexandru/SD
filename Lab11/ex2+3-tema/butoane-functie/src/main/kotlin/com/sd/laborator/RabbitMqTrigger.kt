package com.sd.laborator

import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import jakarta.inject.Inject

@RabbitListener
class RabbitMqTrigger {

    @Inject
    lateinit var functie_butoane: ButtonsFunction

    @Queue("coada_butoane")
    fun onMessage(event: ButtonDTO) {
        println("S-a primit un mesaj din RabbitMQ. Se declanșează funcția serverless...")

        functie_butoane.accept(event)
    }
}