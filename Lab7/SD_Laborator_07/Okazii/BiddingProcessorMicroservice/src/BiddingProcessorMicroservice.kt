import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.system.exitProcess

class BiddingProcessorMicroservice {
    private var biddingProcessorSocket: ServerSocket
    private lateinit var auctioneerSocket: Socket
    private var receiveProcessedBidsObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val processedBidsQueue: Queue<Message> = LinkedList<Message>()

    companion object Constants {
        const val BIDDING_PROCESSOR_PORT = 1700
        const val AUCTIONEER_PORT = 1500
        const val AUCTIONEER_HOST = "auctioneer"
        const val numeJurnal = "bidding_processor_microservice"
    }

    init {
        biddingProcessorSocket = ServerSocket(BIDDING_PROCESSOR_PORT)
        println("BiddingProcessorMicroservice se executa pe portul: ${biddingProcessorSocket.localPort}")
        println("Se asteapta ofertele pentru finalizarea licitatiei...")

        Jurnal.creazaFisier(numeJurnal)

        pornestePortDeSanatate(1710)

        // se asteapta mesaje primite de la MessageProcessorMicroservice
        val messageProcessorConnection = biddingProcessorSocket.accept()
        val bufferReader = BufferedReader(InputStreamReader(messageProcessorConnection.inputStream))

        // se creeaza obiectul Observable cu care se captureaza mesajele de la MessageProcessorMicroservice
        receiveProcessedBidsObservable = Observable.create<String> { emitter ->
            while (true) {
                // se citeste mesajul de la MessageProcessorMicroservice de pe socketul TCP
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    // deci MessageProcessorMicroservice a fost deconectat
                    bufferReader.close()
                    messageProcessorConnection.close()

                    Jurnal.scrieInFisier(numeJurnal, "EROARE", "Eroare: MessageProcessorMicroservice ${messageProcessorConnection.port} a fost deconectat.")
                    emitter.onError(Exception("Eroare: MessageProcessorMicroservice ${messageProcessorConnection.port} a fost deconectat."))
                    break
                }

                // daca mesajul este cel de tip „FINAL DE LISTA DE MESAJE” (avand corpul "final"), atunci se emite semnalul Complete
                if (Message.deserialize(receivedMessage.toByteArray()).body == "final") {
                    emitter.onComplete()

                    // s-au primit toate mesajele de la MessageProcessorMicroservice, i se trimite un mesaj pentru a semnala
                    // acest lucru
                    val finishedBidsMessage = Message.create(
                        "${messageProcessorConnection.localAddress}:${messageProcessorConnection.localPort}",
                        "am primit tot"
                    )

                    messageProcessorConnection.getOutputStream().write(finishedBidsMessage.serialize())
                    messageProcessorConnection.close()

                    break
                } else {
                    // se emite ce s-a citit ca si element in fluxul de mesaje
                    emitter.onNext(receivedMessage)
                }
            }
        }
    }

    private fun pornestePortDeSanatate(portSanatate: Int) {
        Thread {
            val healthSocket = ServerSocket(portSanatate)
            while (true) {
                try {
                    val ping = healthSocket.accept()
                    ping.close()
                } catch (e: Exception) {
                    break
                }
            }
        }.start()
    }

    private fun receiveProcessedBids() {
        // se primesc si se adauga in coada ofertele procesate de la MessageProcessorMicroservice
        val receiveProcessedBidsSubscription = receiveProcessedBidsObservable
            .subscribeBy(
                onNext = {
                    val message = Message.deserialize(it.toByteArray())
                    println(message)
                    Jurnal.scrieInFisier(numeJurnal, "MESAJ PRIMIT", message.toString())
                    processedBidsQueue.add(message)
                },
                onComplete = {
                    // s-a incheiat primirea tuturor mesajelor
                    // se decide castigatorul licitatiei
                    Jurnal.scrieInFisier(numeJurnal, "PENDING", "Incep decizia castigatorului.")
                    decideAuctionWinner()
                },
                onError = { println("Eroare: $it")
                    Jurnal.scrieInFisier(numeJurnal, "ERROR", "$it.")
                }

            )
        subscriptions.add(receiveProcessedBidsSubscription)
    }

    private fun decideAuctionWinner() {
        // se calculeaza castigatorul ca fiind cel care a ofertat cel mai mult
        val winner: Message? = processedBidsQueue.toList().maxByOrNull {
            // corpul mesajului e de forma "licitez <SUMA_LICITATA>"
            // se preia a doua parte, separata de spatiu
            it.body.split(" ")[1].toInt()
        }

        Jurnal.scrieInFisier(numeJurnal, "DONE", "Castigatorul este: ${winner?.sender}")
        println("Castigatorul este: ${winner?.sender}")

        try {
            auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)

            // se trimite castigatorul catre AuctioneerMicroservice
            auctioneerSocket.getOutputStream().write(winner!!.serialize())
            auctioneerSocket.close()

            println("Am anuntat castigatorul catre AuctioneerMicroservice.")
        } catch (e: Exception) {
            println("Nu ma pot conecta la Auctioneer!")
            biddingProcessorSocket.close()
            exitProcess(1)
        }
    }

    fun run() {
        val dateSalvate = Jurnal.verificaDacaApicat(numeJurnal)

        if (dateSalvate != null) {
            println("[RECOVERY] Am detectat o oprire in BiddingProcessor in stadiul: $dateSalvate")
            Jurnal.scrieInFisier(numeJurnal, "RECOVERY", "Eroare la calcul. Se curata starea: $dateSalvate")

            processedBidsQueue.clear()

            Jurnal.scrieInFisier(numeJurnal, "DONE", "Sistem resetat. Astept oferte noi.")
        }

        Jurnal.stergeDinFisier(numeJurnal)

        Jurnal.scrieInFisier(numeJurnal, "START", "Microserviciul a pornit pe portul ${biddingProcessorSocket.localPort}")

        receiveProcessedBids()

        val oprireHeartbeat = Socket("heartbeat-monitor", 1800)
        oprireHeartbeat.close()
        println("Am trimis semnalul de oprire către HeartbeatMonitor.")

        // se elibereaza memoria din multimea de Subscriptions
        subscriptions.dispose()
        Jurnal.scrieInFisier(numeJurnal, "FINISH", "Am terminat de executat tot.")

        exitProcess(0)
    }
}

fun main(args: Array<String>) {
    val biddingProcessorMicroservice = BiddingProcessorMicroservice()
    biddingProcessorMicroservice.run()
}