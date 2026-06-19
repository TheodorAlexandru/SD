package com.sd.laborator

import io.micronaut.function.FunctionBean
import jakarta.inject.Inject
import java.util.function.Consumer
import org.slf4j.LoggerFactory

// Expunem clasa ca funcție serverless
@FunctionBean("procesareClick")
class ButtonsFunction : Consumer<ButtonDTO> {

    @Inject
    lateinit var repository: ButtonRepositoryInterface

    private val LOG = LoggerFactory.getLogger(ButtonsFunction::class.java)

    override fun accept(event: ButtonDTO) {
        LOG.info("A fost primit un eveniment pentru butonul: ${event.buttonName}")

        var row = repository.findByNumeButon(event.buttonName)

        if (row != null) {
            row.numarApasari += 1
        } else {
            // Dacă nu există, îl creăm cu o primă apăsare
            row = ButtonRepository(numeButon = event.buttonName, numarApasari = 1)
        }

        // Salvăm modificarea în MySQL
        repository.update(row)
        LOG.info("Actualizat: ${row.numeButon} -> ${row.numarApasari} apăsări.")
    }
}