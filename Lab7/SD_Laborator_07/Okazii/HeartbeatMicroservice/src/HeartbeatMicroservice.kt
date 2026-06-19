import io.reactivex.rxjava3.core.Observable
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class HeartbeatMicroservice {

    private val _nrBidderi = 5

    private val listaBidderi = (1.._nrBidderi).associate {
        "Bidder $it" to Pair("okazii-bidder-$it",1810)
    }

    // Lista cu serviciile si "porturile lor de sanatate"
    private val serviciiDeVerificat = mapOf(
        "Auctioneer" to Pair("auctioneer", 1510),
        "MessageProcessor" to Pair("message-processor", 1610),
        "BiddingProcessor" to Pair("bidding-processor", 1710)
    ) + listaBidderi

    fun run() {
        println("Heartbeat Monitor a pornit. Verific starea sistemelor o data la 5 secunde...")

        // Folosim RxJava pentru a declansa un eveniment la fiecare 5 secunde
        val subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
            .subscribe {
                println("\n[PING] Verificare sisteme")

                serviciiDeVerificat.forEach { (numeServiciu, adresa) ->
                    val host = adresa.first
                    val port = adresa.second

                    try {
                        // Incercam sa deschidem un socket scurt catre portul de sanatate
                        val socket = Socket(host, port)
                        socket.close()
                        println("$numeServiciu este ONLINE")
                    } catch (e: Exception) {
                        println("ALARMA: $numeServiciu este OFFLINE!")
                    }
                }
            }

        val stopSocket = ServerSocket(1800)
        stopSocket.accept()

        println("Licitația s-a încheiat! Am primit semnalul de oprire. Închid Heartbeat-ul.")
        subscription.dispose()
        stopSocket.close()
        exitProcess(0)
    }
}

fun main() {
    val heartbeat = HeartbeatMicroservice()
    heartbeat.run()
}