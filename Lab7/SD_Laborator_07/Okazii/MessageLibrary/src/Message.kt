import java.text.SimpleDateFormat
import java.util.*

class Message private constructor(val sender: String, val body: String, val timestamp: Date, val name: String, val email: String, val telefon: String) {
    companion object {
        fun create(sender: String, body: String, name: String = "-", email : String = "-", telefon: String = "-"): Message {
            return Message(sender, body, Date(), name, email, telefon)
        }

        fun deserialize(msg: ByteArray): Message {
            val msgString = String(msg)
            val parts = msgString.split('|', limit = 6)
            val timestamp = parts[0]
            val name = parts[1]
            val email = parts[2]
            val telefon = parts[3]
            val sender = parts[4]
            val body = parts[5]

            return Message(sender, body, Date(timestamp.toLong()), name, email, telefon)
        }
    }

    fun serialize(): ByteArray {
        return "${timestamp.time}|$name|$email|$telefon|$sender|$body\n".toByteArray()
    }

    override fun toString(): String {
        val dateString = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp)
        return "[$dateString] [$name, $email, $telefon] $sender >>> $body"
    }
}

fun main(args: Array<String>) {
    val msg = Message.create("localhost:4848", "test mesaj")
    println(msg)
    val serialized = msg.serialize()
    val deserialized = Message.deserialize(serialized)
    println(deserialized)
}