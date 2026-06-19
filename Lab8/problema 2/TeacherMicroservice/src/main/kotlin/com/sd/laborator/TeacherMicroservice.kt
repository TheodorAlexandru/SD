package com.sd.laborator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherMicroservice {
    // intrebarile si raspunsurile sunt mentinute intr-o lista de perechi de forma:
    // [<INTREBARE 1, RASPUNS 1>, <INTREBARE 2, RASPUNS 2>, ... ]
    private lateinit var questionDatabase: MutableList<Pair<String, String>>
    private lateinit var messageManagerSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    init {
        val databaseLines: List<String> = File("questions_database.txt").readLines()
        questionDatabase = mutableListOf()

        /*
         "baza de date" cu intrebari si raspunsuri este de forma:

         <INTREBARE_1>\n
         <RASPUNS_INTREBARE_1>\n
         <INTREBARE_2>\n
         <RASPUNS_INTREBARE_2>\n
         ...
         */
        for (i in 0..(databaseLines.size - 1) step 2) {
            questionDatabase.add(Pair(databaseLines[i], databaseLines[i + 1]))
        }
    }

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1600
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun respondToQuestion(question: String): String? {
        questionDatabase.forEach {
            // daca se gaseste raspunsul la intrebare, acesta este returnat apelantului
            if (it.first == question) {
                return it.second
            }
        }
        return null
    }

    public fun run() = runBlocking {
        launch(Dispatchers.IO) {
            try {
                // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
                subscribeToMessageManager()

                println("TeacherMicroservice asculta pe portul: ${messageManagerSocket.localPort}")
                println("Se asteapta mesaje...")

                val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

                while (isActive) {
                    // se asteapta intrebari trimise prin intermediarul "MessageManager"
                    val response = bufferReader.readLine()

                    if (response == null) {
                        // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                        println("Microserviciul MessageService (${messageManagerSocket.port}) a fost oprit.")
                        bufferReader.close()
                        messageManagerSocket.close()
                        break
                    }

                    // se foloseste o corutina separata pentru tratarea intrebarii primite
                    launch(Dispatchers.IO) {
                        val (messageType, messageDestination, messageBody) = response.split(" ", limit = 3)

                        when (messageType) {
                            // tipul mesajului cunoscut de acest microserviciu este de forma:
                            // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                            "intrebare" -> {
                                println("Am primit o intrebare de la $messageDestination: \"${messageBody}\"")
                                var responseToQuestion = respondToQuestion(messageBody)
                                responseToQuestion?.let {
                                    responseToQuestion = "raspuns $messageDestination $it"
                                    println("Trimit raspunsul: \"${response}\"")
                                    messageManagerSocket.getOutputStream()
                                        .write((responseToQuestion + "\n").toByteArray())
                                }
                            }
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                println("Eroare in firul de ascultare la MessageManager: ${e.message}")
            }
        }

        launch(Dispatchers.IO) {
            val teacherServerSocket = ServerSocket(TEACHER_PORT)
            println("TeacherMicroservice asteapta cereri pe portul: ${teacherServerSocket.localPort}")
            println("Se asteapta cereri (intrebari)...")
            while (isActive) {
                // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
                // (in acest caz, din partea aplicatiei client GUI)
                val clientConnection = teacherServerSocket.accept()

                // se foloseste o corutina separata pentru tratarea fiecarei conexiuni client
                launch (Dispatchers.IO) {
                    println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                    // se citeste intrebarea dorita
                    val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                    val receivedQuestion = clientBufferReader.readLine() ?: return@launch

                    val interograreSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
                    interograreSocket.soTimeout = 3000

                    // intrebarea este redirectionata catre microserviciul MessageManager
                    println("Trimit catre MessageManager: ${"intrebare ${interograreSocket.localPort} $receivedQuestion\n"}")
                    interograreSocket.getOutputStream()
                        .write(("intrebare ${interograreSocket.localPort} $receivedQuestion\n").toByteArray())

                    // se asteapta raspuns de la MessageManager
                    val messageManagerBufferReader = BufferedReader(InputStreamReader(interograreSocket.inputStream))
                    try {
                        val receivedResponse = messageManagerBufferReader.readLine()

                        // se trimite raspunsul inapoi clientului apelant
                        println("Am primit raspunsul: \"$receivedResponse\"")
                        clientConnection.getOutputStream().write((receivedResponse + "\n").toByteArray())
                    } catch (e: SocketTimeoutException) {
                        println("Nu a venit niciun raspuns in timp util.")
                        clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                    } finally {
                        // se inchide conexiunea cu clientul
                        clientConnection.close()
                        interograreSocket.close()
                    }
                }
            }
        }

    }
}

fun main() {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}