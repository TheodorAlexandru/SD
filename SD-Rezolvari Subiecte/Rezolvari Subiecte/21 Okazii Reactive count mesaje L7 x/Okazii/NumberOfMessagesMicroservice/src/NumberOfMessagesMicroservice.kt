import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

class NumberOfMessagesMicroservice {
    private var count = 0
    private val serverSocket = ServerSocket(1650)
    private val clients = mutableListOf<BufferedReader>()

    companion object {
        const val PROCESSOR_PORT = 1650
    }

    init {
        serverSocket.soTimeout = 1000 // timeout mic ca sa putem verifica toti clientii
        println("NumberOfMessagesMicroservice se executa pe portul: $PROCESSOR_PORT")
        println("Se asteapta celelalte servicii sa se conecteze...")

        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < 60_000) {
            // incearca sa accepte conexiuni noi
            try {
                val client = serverSocket.accept()
                val reader = BufferedReader(InputStreamReader(client.inputStream))
                clients.add(reader)
                println("Serviciu conectat!")
            } catch (e: SocketTimeoutException) {
                // nu a venit nicio conexiune noua, continuam
            }

            // citeste ce e disponibil de la toti clientii conectati
            for (reader in clients) {
                while (reader.ready()) {
                    val line = reader.readLine()
                    if (line == "count") {
                        count++
                        println("Count curent: $count")
                    }
                }

            }
        }

        println("finalul licitatiei")
        serverSocket.close()
    }
}

fun main(args: Array<String>) {
    NumberOfMessagesMicroservice()
}