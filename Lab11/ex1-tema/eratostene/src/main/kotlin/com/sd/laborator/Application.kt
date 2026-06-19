package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut
import jakarta.inject.Inject

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @Controller("/eratostene")
    class LambdaController {
        @Inject
        lateinit var handler : EratosteneFunction

        @Post
        fun execute(@Body request: EratosteneRequest):
                EratosteneResponse {
            return handler.apply(request)
        }

    }
}