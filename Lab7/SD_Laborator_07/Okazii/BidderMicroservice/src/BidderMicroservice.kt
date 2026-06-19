import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.Date
import kotlin.Exception
import kotlin.random.Random
import kotlin.system.exitProcess

class BidderMicroservice {
    private lateinit var auctioneerSocket: Socket
    private lateinit var containerID: String
    private var auctionResultObservable: Observable<String>
    private var myIdentity: String = "[BIDDER_NECONECTAT]"
    companion object Constants {
        const val AUCTIONEER_HOST = "auctioneer"
        const val AUCTIONEER_PORT = 1500
        const val MAX_BID = 10_000
        const val MIN_BID = 1_000
        const val numeJurnal = "bidder_microservice_"
        val listaNumeBidderi: List<String> = listOf("Popescu Ion", "Ionescu Marcel", "Pop Alexandru", "Munteanu Delia", "Baciu Ioana", "Popa Alexandra", "Rusu Maria", "Dumitriu Costel", "Mocanu Marian", "Stan Dumitru", "Suciu Sebastian", "Hiera Lucian", "Gheorghe Tudor", "Ungureanu Maia", "Cristea Radu")
        val listaTipEMailBidderi: List<String> = listOf("gmail.com", "yahoo.com", "hotmail.com", "tuiasi.ro", "nuvreau.ro", "cefaci.com", "muuuu.com")
    }

    init {
        try {
            var conectat = false
            while (!conectat) {
                try {
                    auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)
                    conectat = true
                    println("M-am conectat la Auctioneer!")
                } catch (e: Exception) {
                    println("Auctioneer nu este gata. Astept 1 secunda si reincerc...")
                    Thread.sleep(1000) // Pauza de 1 secunda inainte de a incerca din nou
                }
            }

            //in cazul in care rulam aplicatia din IntelliJ, nu putem prelua HOSTNAME-ul si facem unul pe loc
            containerID = System.getenv("HOSTNAME") ?: "local_${Random.nextInt(1000)}"

            myIdentity = "[BIDDER-$containerID]"

            Jurnal.creazaFisier(numeJurnal + containerID)

            pornestePortDeSanatate(1810)

            // se creeaza un obiect Observable ce va emite mesaje primite printr-un TCP
            // fiecare mesaj primit reprezinta un element al fluxului de date reactiv
            auctionResultObservable = Observable.create<String> { emitter ->
                // se citeste raspunsul de pe socketul TCP
                val bufferReader = BufferedReader(InputStreamReader(auctioneerSocket.inputStream))
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    bufferReader.close()
                    auctioneerSocket.close()

                    emitter.onError(Exception("AuctioneerMicroservice s-a deconectat."))
                    return@create
                }

                // mesajul primit este emis in flux
                emitter.onNext(receivedMessage)

                // deoarece se asteapta un singur mesaj, in continuare se emite semnalul de incheiere al fluxului
                emitter.onComplete()

                bufferReader.close()
                auctioneerSocket.close()
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la Auctioneer!")
            exitProcess(1)
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

    private fun bid() {
        //cu .hashCode(), luam un cuvant si il transformam in numar intreg
        val seed = containerID.hashCode().toLong()
        val numar = kotlin.random.Random(seed)

        // se genereaza o oferta aleatorie din partea bidderului curent
        val pret = numar.nextInt(MIN_BID, MAX_BID)
        val bidderName = listaNumeBidderi.random(numar)
        val bidderEmail = bidderName.replace(" ", "").lowercase() + "@" + listaTipEMailBidderi.random(numar)
        val nrTel = "07" + numar.nextInt(10000000, 99999999).toString()

        // se creeaza mesajul care incapsuleaza oferta
        val biddingMessage = Message.create("${auctioneerSocket.localAddress}:${auctioneerSocket.localPort}", "licitez $pret", bidderName, bidderEmail, nrTel)
        Jurnal.scrieInFisier(numeJurnal + containerID, "PENDING", "Oferta Bidderului: $biddingMessage")

        // bidder-ul trimite pretul pentru care doreste sa liciteze
        val serializedMessage = biddingMessage.serialize()
        auctioneerSocket.getOutputStream().write(serializedMessage)

        // exista o sansa din 2 ca bidder-ul sa-si trimita oferta de 2 ori, eronat
        if (numar.nextBoolean()) {
            auctioneerSocket.getOutputStream().write(serializedMessage)
        }
    }

    private fun waitForResult() {
        println("$myIdentity Astept rezultatul licitatiei...")
        // bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa de acesta
        val auctionResultSubscription = auctionResultObservable.subscribeBy(
            // cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
            onNext = {
                val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("$myIdentity Rezultat licitatie: ${resultMessage.body}")
                Jurnal.scrieInFisier(numeJurnal + containerID, "DONE", "$myIdentity Rezultat licitatie: ${resultMessage.body}")
            },
            onError = {
                println("$myIdentity Eroare: $it")
                Jurnal.scrieInFisier(numeJurnal + containerID, "ERROR", "$myIdentity Eroare: $it")

                exitProcess(1)
            }
        )

        // se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    fun run() {

        val dateSalvate = Jurnal.verificaDacaApicat(numeJurnal + containerID)

        if (dateSalvate != null) {
            println("[RECOVERY] Am detectat o oprire neasteptata! Retrimit oferta...")
            Jurnal.scrieInFisier(numeJurnal + containerID, "RECOVERY", "Am detectat o oprire neasteptata! Retrimit oferta...")

            Jurnal.scrieInFisier(numeJurnal + containerID, "DONE", "Astept repornirea serverului...")
        }

        Jurnal.stergeDinFisier(numeJurnal + containerID)

        Jurnal.scrieInFisier(numeJurnal + containerID, "START", "Microserviciul a pornit pe portul ${auctioneerSocket.localPort}")
        bid()
        waitForResult()
        Jurnal.scrieInFisier(numeJurnal + containerID, "FINISH", "Am terminat de executat tot.")

        exitProcess(0)
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = BidderMicroservice()
    bidderMicroservice.run()
}