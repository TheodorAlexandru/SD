package com.sd.laborator

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.util.function.Consumer


@FunctionBean
class EratosteneQueueFunction: FunctionInitializer(),
    Consumer<EratosteneQueueRequest>{
    private val LOG: Logger = LoggerFactory.getLogger(EratosteneQueueFunction::class.java)

    @Inject
    lateinit var queueClient: EratosteneQueueClient

    override fun accept(msg: EratosteneQueueRequest){
        try{
            val numbers = msg.getNumbersToCheck()

            //numarul pana la care se calculeaza toate numerele prime din functia micronaut Eratostene
            val maxNumber = numbers?.maxOrNull() ?: 100

            val date = mapOf("number" to maxNumber + 1, "numbersToCheck" to (numbers ?: emptyList()))
            LOG.info("Coada trimite date mai departe catre portul 8082")
            queueClient.trimiteCatreEratostene(date)

        } catch (e: Exception) {
            LOG.error("Eroare: ${e.message}")
        }
    }
}


