package com.sd.laborator
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }
    @Controller("/eratosteneQueue")
    class LambdaCOntroller{
        companion object {
            private val handler = EratosteneQueueFunction()
        }

        @Post
        fun execute(@Body request: EratosteneQueueRequest){
            handler.accept(request)
        }
    }

}