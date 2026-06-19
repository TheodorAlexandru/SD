package com.sd.laborator

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

// Interfata pe care Micronaut o implementeaza automat pentru a trimite mesaje in Kafka
@KafkaClient
interface KafkaProducer {
    fun sendInput(@Topic topic: String, request: StateRequest)
}

@Controller("/machine")
class MachineController(@Inject val producer: KafkaProducer) {
    var currentState = "00"

    // REST API pentru utilizator
    @Post("/input/{val}")
    fun receiveInput(`val`: Int): String {
        val topic = "stare_$currentState"
        producer.sendInput(topic, StateRequest(`val`))
        return "Input $`val` trimis spre $topic. Stare curenta: $currentState\n"
    }

    // Endpoint intern folosit de functii pentru a actualiza starea globala
    @Post("/update/{newState}")
    fun updateState(newState: String) {
        currentState = newState
    }
}