package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class HeartbeatMicroservice {
    private lateinit var messageManagerSocket: Socket

    // Map pentru a reține portul fiecărui serviciu și momentul (în milisecunde) când a dat ultimul "semn de viață"
    private val activeServices = ConcurrentHashMap<Int, Long>()

    companion object Constants {
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500 // Portul pe care ascultă MessageManager [cite: 1133]
        const val PING_INTERVAL = 5000L       // Timpul între verificări (5 secunde)
        const val TIMEOUT_THRESHOLD = 15000L  // Timpul după care considerăm că un serviciu a picat (15 secunde)
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu m-am putut conecta la MessageManager!")
            exitProcess(1)
        }
    }

    fun run() {
        // Ne înscriem la MessageManager conectându-ne la el [cite: 1252]
        subscribeToMessageManager()

        // 1. Thread pentru citirea răspunsurilor de la microserviciile active
        thread {
            val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
            while (true) {
                try {
                    val response = bufferReader.readLine() ?: break

                    // Așteptăm un mesaj de forma: "pong <PORT_SERVICIU>"
                    if (response.startsWith("pong")) {
                        val parts = response.split(" ")
                        if (parts.size >= 2) {
                            val servicePort = parts[1].toInt()
                            // Actualizăm timestamp-ul pentru acest serviciu
                            activeServices[servicePort] = System.currentTimeMillis()
                            println("Heartbeat: Serviciul de pe portul $servicePort este activ.")
                        }
                    }
                } catch (e: Exception) {
                    println("Heartbeat: Eroare la citirea mesajelor.")
                    break
                }
            }
        }

        // 2. Thread pentru trimiterea mesajelor false (ping) și verificarea stării
        thread {
            while (true) {
                Thread.sleep(PING_INTERVAL)

                // MessageManager va redirecționa acest mesaj către toți clienții înscriși
                val pingMessage = "intrebare 0 ping\n"

                try {
                    messageManagerSocket.getOutputStream().write(pingMessage.toByteArray())
                    println("Heartbeat: Am trimis PING către toate serviciile.")
                } catch (e: Exception) {
                    println("Heartbeat: Eroare la trimiterea PING-ului.")
                }

                // Verificăm dacă vreun serviciu a depășit timpul limită (TIMEOUT_THRESHOLD)
                val currentTime = System.currentTimeMillis()
                val iterator = activeServices.iterator()

                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    val servicePort = entry.key
                    val lastSeen = entry.value

                    if (currentTime - lastSeen > TIMEOUT_THRESHOLD) {
                        println("Heartbeat ALARMĂ: Serviciul de pe portul $servicePort nu a răspuns la timp! (Inactiv)")
                        // Eliminăm serviciul din listă pentru a nu spama consola
                        // Ulterior, aici vom adăuga logica de a cere replicarea
                        iterator.remove()
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val heartbeatMicroservice = HeartbeatMicroservice()
    heartbeatMicroservice.run()
}