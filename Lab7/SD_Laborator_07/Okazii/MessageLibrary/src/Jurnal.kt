import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.xml.soap.Detail

object Jurnal {
    private val caleaCatreProiect = "Logs"

    fun creazaFisier(numeFisier: String){
        val logFile = File("${caleaCatreProiect}/${numeFisier}.txt")
        if(!logFile.exists()) logFile.createNewFile()
    }

    fun scrieInFisier(numeFisier: String, stare: String, mesaj: String){
        val logFile = File("${caleaCatreProiect}/${numeFisier}.txt")
        val timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date())

        logFile.appendText("[$timestamp] $stare | $mesaj\n")
    }

    fun stergeDinFisier(numeFisier: String){
        val logFile = File("${caleaCatreProiect}/${numeFisier}.txt")
        if(logFile.exists() && !logFile.readText().isEmpty())
            logFile.writeText("")
    }

    fun citesteUltimaLinie(numeFisier: String): String? {
        val logFile = File("$caleaCatreProiect/$numeFisier.txt")
        if (!logFile.exists() || logFile.readText().isEmpty()) {
            return null
        }
        return logFile.readLines().last()
    }

    fun verificaDacaApicat(numeFisier: String): String? {
        val ultimaLinie = citesteUltimaLinie(numeFisier)

        if (ultimaLinie != null && ultimaLinie.contains("PENDING")) {
            return ultimaLinie.substringAfter("|").trim()
        }
        return null
    }
}