package com.sd.laborator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class MessageManagerMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private lateinit var messageManagerSocket: ServerSocket

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
    }

    init {
        subscribers = hashMapOf()
    }

    private fun broadcastMessage(message: String, exceptIp: String) {
        subscribers.forEach {
            if(it.value.inetAddress.hostAddress != exceptIp) {
                it.value.getOutputStream()?.write((message + "\n").toByteArray())
            }
        }
    }

    private fun respondTo(destination: Int, message: String) {
        subscribers[destination]?.getOutputStream()?.write((message + "\n").toByteArray())
    }

    private fun privateMessage(message: String, destination: String) {
        subscribers.forEach {
            if(it.value.inetAddress.hostAddress == destination) {
                it.value.getOutputStream()?.write((message + "\n").toByteArray())
            }
        }
    }

    public fun run() = runBlocking {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        messageManagerSocket = ServerSocket(MESSAGE_MANAGER_PORT)
        println("MessageManagerMicroservice se executa pe portul: ${messageManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (isActive) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = messageManagerSocket.accept()

            // se porneste o corutina separata pentru tratarea conexiunii cu clientul
            launch(Dispatchers.IO) {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (isActive) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        synchronized(subscribers) {
                            subscribers.remove(clientConnection.port)
                        }
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("Primit mesaj: $receivedMessage")
                    val (messageType, messageDestination, messageBody) = receivedMessage.split(" ", limit = 3)

                    when (messageType) {
                        "intrebare" -> {
                            // tipul mesajului de tip intrebare este de forma:
                            // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                            broadcastMessage("intrebare ${clientConnection.port} $messageBody", exceptIp = clientConnection.inetAddress.hostAddress)
                        }
                        "raspuns" -> {
                            // tipul mesajului de tip raspuns este de forma:
                            // raspuns <CONTINUT_RASPUNS>
                            respondTo(messageDestination.toInt(), messageBody)
                        }
                        "intrebare_privata" -> {
                            // tipul mesajului de tip intrebare este de forma:
                            // intrebare_privata <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                            privateMessage("intrebare ${clientConnection.port} $messageBody", destination = messageDestination)
                        }
                        "status_request" -> {
                            // 1. Colectam IP-urile si porturile conectate
                            val listaAdrese = synchronized(subscribers) {
                                subscribers.values.map { "${it.inetAddress.hostAddress}:${it.port}" }.joinToString(",")
                            }

                            val raspuns = if (listaAdrese.isEmpty()) "NIMENI" else listaAdrese
                            clientConnection.getOutputStream().write(("status_response $raspuns\n").toByteArray())
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}
