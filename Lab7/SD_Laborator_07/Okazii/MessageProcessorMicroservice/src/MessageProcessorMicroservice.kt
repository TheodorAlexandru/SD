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

class MessageProcessorMicroservice {
    private var messageProcessorSocket: ServerSocket
    private lateinit var biddingProcessorSocket: Socket
    private var auctioneerConnection:Socket
    private var receiveInQueueObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val messageQueue: Queue<Message> = LinkedList<Message>()

    companion object Constants {
        const val MESSAGE_PROCESSOR_PORT = 1600
        const val BIDDING_PROCESSOR_HOST = "bidding-processor"
        const val BIDDING_PROCESSOR_PORT = 1700
        const val numeJurnal = "message_processor_microservice"
    }

    init {
        messageProcessorSocket = ServerSocket(MESSAGE_PROCESSOR_PORT)
        println("MessageProcessorMicroservice se executa pe portul: ${messageProcessorSocket.localPort}")
        println("Se asteapta mesaje pentru procesare...")

        Jurnal.creazaFisier(numeJurnal)

        pornestePortDeSanatate(1610)

        // se asteapta mesaje primite de la AuctioneerMicroservice
        auctioneerConnection = messageProcessorSocket.accept()
        val bufferReader = BufferedReader(InputStreamReader(auctioneerConnection.inputStream))

        // se creeaza obiectul Observable cu care se captureaza mesajele de la AuctioneerMicroservice
        receiveInQueueObservable = Observable.create<String> { emitter ->
            while (true) {
                // se citeste mesajul de la AuctioneerMicroservice de pe socketul TCP
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    // deci subscriber-ul respectiv a fost deconectat
                    bufferReader.close()
                    auctioneerConnection.close()

                    emitter.onError(Exception("Eroare: AuctioneerMicroservice ${auctioneerConnection.port} a fost deconectat."))
                    Jurnal.scrieInFisier(numeJurnal, "EROARE", "Eroare: AuctioneerMicroservice ${auctioneerConnection.port} a fost deconectat.")
                    break
                }

                // daca mesajul este cel de incheiere a licitatiei (avand corpul "final"), atunci se emite semnalul Complete
                if (Message.deserialize(receivedMessage.toByteArray()).body == "final") {
                    emitter.onComplete()

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

    private fun receiveAndProcessMessages() {
        // se primesc si se adauga in coada mesajele de la AuctioneerMicroservice
        val receiveInQueueSubscription = receiveInQueueObservable
            ///TODO --- filtrati duplicatele folosind operatorul de filtrare
            .distinct()
            .subscribeBy(
                onNext = {
                    val message = Message.deserialize(it.toByteArray())
                    println(message)
                    Jurnal.scrieInFisier(numeJurnal, "PENDING","Procesez $message")
                    messageQueue.add(message)
                },
                onComplete = {
                    // s-a incheiat primirea tuturor mesajelor
                    ///TODO --- se ordoneaza in functie de data si ora cand mesajele au fost primite
                    val mesajeSortate = messageQueue.sortedBy { it.timestamp }
                    messageQueue.clear()
                    messageQueue.addAll(mesajeSortate)
                    // s-au primit toate mesajele de la AuctioneerMicroservice, i se trimite un mesaj pentru a semnala
                    // acest lucru
                    val finishedMessagesMessage = Message.create(
                        "${auctioneerConnection.localAddress}:${auctioneerConnection.localPort}",
                        "am primit tot"
                    )
                    auctioneerConnection.getOutputStream().write(finishedMessagesMessage.serialize())
                    auctioneerConnection.close()

                    Jurnal.scrieInFisier(numeJurnal, "PENDING", "Am primit toate mesajele")
                    // se trimit mai departe mesajele procesate catre BiddingProcessor
                    sendProcessedMessages()
                },
                onError = { println("Eroare: $it")
                            Jurnal.scrieInFisier(numeJurnal, "ERROR","$it")}
            )
        subscriptions.add(receiveInQueueSubscription)
    }

    private fun sendProcessedMessages() {
        try {
            var conectat = false
            while (!conectat) {
                try {
                    biddingProcessorSocket = Socket(BIDDING_PROCESSOR_HOST, BIDDING_PROCESSOR_PORT)
                    conectat = true
                } catch (e: Exception) {
                    println("BiddingProcessor este offline! Astept 2 secunde sa il reporneasca Docker...")
                    Thread.sleep(2000)
                }
            }

            println("Trimit urmatoarele mesaje:")
            Observable.fromIterable(messageQueue).subscribeBy(
                onNext = {
                    println(it.toString())
                    Jurnal.scrieInFisier(numeJurnal, "DONE", "Am procesat ${it}.")

                    // trimitere mesaje catre procesorul licitatiei, care decide rezultatul final
                    biddingProcessorSocket.getOutputStream().write(it.serialize())
                },
                onComplete = {
                    val noMoreMessages = Message.create(
                        "${biddingProcessorSocket.localAddress}:${biddingProcessorSocket.localPort}",
                        "final"
                    )
                    Jurnal.scrieInFisier(numeJurnal, "DONE", "Am terminat de procesat mesajele.")
                    biddingProcessorSocket.getOutputStream().write(noMoreMessages.serialize())
                    biddingProcessorSocket.close()

                    // se elibereaza memoria din multimea de Subscriptions
                    subscriptions.dispose()
                }
            )
        } catch (e: Exception) {
            println("Nu ma pot conecta la BiddingProcessor!")
            messageProcessorSocket.close()
            exitProcess(1)
        }
    }

    fun run() {

        val dateSalvate = Jurnal.verificaDacaApicat(numeJurnal)

        if (dateSalvate != null) {
            println("[RECOVERY] Am detectat o oprire neasteptata! Recuperez mesajul blocat...")
            Jurnal.scrieInFisier(numeJurnal, "RECOVERY", "Incarc starea anterioara: $dateSalvate")

            messageQueue.clear()

            Jurnal.scrieInFisier(numeJurnal, "DONE", "Am curatat coada de mesaje. Astept repornirea programului.")
        }

        Jurnal.stergeDinFisier(numeJurnal)

        Jurnal.scrieInFisier(numeJurnal, "START", "Microserviciul a pornit pe portul ${messageProcessorSocket.localPort}")
        receiveAndProcessMessages()
        Jurnal.scrieInFisier(numeJurnal, "FINISH", "Am terminat de executat tot.")

        exitProcess(0)
    }
}

fun main(args: Array<String>) {
    val messageProcessorMicroservice = MessageProcessorMicroservice()
    messageProcessorMicroservice.run()
}