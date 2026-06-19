package com.sd.laborator

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.runtime.Micronaut
import jakarta.inject.Inject

// curl http://localhost:8080/

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("com.sd.laborator")
            .mainClass(Application.javaClass)
            .start()
    }
}

@Controller
class LambdaController {
    @Inject
    lateinit var service: EratosteneSieveService

    @Get("/")
    fun run(): Any {
        val result = service.gasirePerechiConditie()
        return mapOf("perechi" to result)
    }
}