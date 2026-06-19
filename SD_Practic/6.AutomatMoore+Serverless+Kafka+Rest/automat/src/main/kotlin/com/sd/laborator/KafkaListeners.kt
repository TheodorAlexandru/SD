package com.sd.laborator

import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Inject

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
class StateConsumers(
    @Inject val state00: State00Function,
    @Inject val state01: State01Function,
    @Inject val state10: State10Function,
    @Inject val state11: State11Function,
    @Client("http://localhost:8080") @Inject val httpClient: HttpClient
) {
    private fun notifyStateChange(newState: String) {
        httpClient.toBlocking().exchange<Any, Any>(HttpRequest.POST("/machine/update/$newState", ""))
    }

    @Topic("stare_00")
    fun process00(req: StateRequest) {
        val res = state00.apply(req) // Apelam functia serverless
        println("[00] Primit ${req.input} -> Tranzitie spre ${res.nextState} | Output: ${res.output}")
        notifyStateChange(res.nextState)
    }

    @Topic("stare_01")
    fun process01(req: StateRequest) {
        val res = state01.apply(req)
        println("[01] Primit ${req.input} -> Tranzitie spre ${res.nextState} | Output: ${res.output}")
        notifyStateChange(res.nextState)
    }

    @Topic("stare_10")
    fun process10(req: StateRequest) {
        val res = state10.apply(req)
        println("[10] Primit ${req.input} -> Tranzitie spre ${res.nextState} | Output: ${res.output}")
        notifyStateChange(res.nextState)
    }

    @Topic("stare_11")
    fun process11(req: StateRequest) {
        val res = state11.apply(req)
        println("[11] Primit ${req.input} -> Tranzitie spre ${res.nextState} | OUTPUT SPECIAL: ${res.output}")
        notifyStateChange(res.nextState)
    }
}