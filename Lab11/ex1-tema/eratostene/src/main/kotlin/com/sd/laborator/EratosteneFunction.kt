package com.sd.laborator

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import jakarta.inject.Inject

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(),
    Function<EratosteneRequest, EratosteneResponse> {
    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService
    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)
    override fun apply(msg: EratosteneRequest): EratosteneResponse {

        // preluare numar din parametrul de intrare al functiei
        val number = msg.getNumber()
        val numbersToCheck = msg.getNumbersToCheck()
        val response = EratosteneResponse()
        // se verifica daca numarul nu depaseste maximul
        if (number >= eratosteneSieveService.MAX_SIZE) {
            LOG.error("Parametru prea mare! $number > maximul de${eratosteneSieveService.MAX_SIZE}")
            response.setMessage("Se accepta doar parametri mai mici ca" + eratosteneSieveService.MAX_SIZE)
            return response
        }
        LOG.info("Se calculeaza numerele prime din fisier")

        val listaNrPrime = eratosteneSieveService.findPrimesLessThan(number)
        val nrPrimeDinFisier = numbersToCheck.filter { listaNrPrime.contains(it) }
        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        response.setPrimes(nrPrimeDinFisier)
        response.setMessage("Calcul efectuat cu succes!")
        LOG.info("Numerele prime din fisier sunt: $nrPrimeDinFisier")
        LOG.info("Calcul incheiat!")
        return response

        /*
        //TEMA DE LABORATOR
        val number = msg.getNumber()
        val response = EratosteneResponse()
        if (number < 1 || number > 9999) {
            LOG.error("trebuie introdus alt numar")
            response.setMessage("Se accepta doar parametri mai mari ca 0 si mai mici ca 10000")
            return response
        }

        LOG.info("Se calculeaza termenii sirului...")
        val vect = mutableListOf<Int>()
        for (i in 0 until number) {
            vect.add(eratosteneSieveService.calculateNUmbersRecursive(i))
        }
        response.setPrimes(vect)
        response.setMessage("Calcul efectuat cu succes!")
        LOG.info("Calcul incheiat")
        return response
        */



        }
    }

    /**
     * This main method allows running the function as a CLI application
    using: echo '{}' | java -jar function.jar
     * where the argument to echo is the JSON to be parsed.
     */
    fun main(args: Array<String>) {
        val function = EratosteneFunction()
        function.run(args, { context ->
            function.apply(context.get(EratosteneRequest::class.java))
        })
    }

