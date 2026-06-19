package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Supplier<EratosteneResponse> {
    @Inject
    private lateinit var service: EratosteneSieveService
    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun get() : EratosteneResponse {
        // preluare numar din parametrul de intrare al functiei

        val response = EratosteneResponse()

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        response.setResponse(service.gasirePerechiConditie())
        response.setMessage("Calcul efectuat cu succes!")

        LOG.info("Calcul incheiat!")

        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) {
    val function = EratosteneFunction()
    function.run(args) { context -> function.get()}
}