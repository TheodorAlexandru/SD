package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class HeartbeatMicroservice {
    private lateinit var messageManagerSocket: Socket

    companion object Constants {
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
    }

    public fun run() = runBlocking {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("Monitorul Heartbeat s-a conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            return@runBlocking
        }

        // Intreaba MessageManager-ul la fiecare 5 secunde
        launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val cerere = "status_request ALL vreau stare microservicii\n"
                    messageManagerSocket.getOutputStream().write(cerere.toByteArray())
                } catch (e: Exception) {
                    println("Eroare la trimiterea interogarii: ${e.message}")
                    break
                }
                delay(5000)
            }
        }

        // Asculta raspunsul de la MessageManager
        launch(Dispatchers.IO) {
            val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
            while (isActive) {
                val response = bufferReader.readLine() ?: break

                // Raspunsul vine sub forma: "status_response 172.19.0.3:1234,172.19.0.4:5678"
                val (messageType, connectedMicroservices) = response.split(" ", limit = 2)

                if (messageType.isNotEmpty() && messageType == "status_response") {

                    println("\n[${java.time.LocalTime.now()}] === RAPORT NODURI ONLINE ===")
                    if (connectedMicroservices == "NIMENI") {
                        println("Nu exista noduri conectate.")
                    } else {
                        // Spargem string-ul delimitat de virgula si afisam fiecare IP
                        val addresses = connectedMicroservices.split(",")
                        addresses.forEach {
                            println(" -> Microserviciu activ: $it")
                        }
                    }
                    println("==========================================\n")
                }
            }
        }
    }
}

fun main() {
    val heartbeatMicroservice = HeartbeatMicroservice()
    heartbeatMicroservice.run()
}