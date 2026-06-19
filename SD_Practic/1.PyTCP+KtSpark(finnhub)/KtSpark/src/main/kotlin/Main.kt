package com.sd.laborator

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream
import java.util.Date

@Serializable
data class News(
    val category: String = "",
    val datetime: Long = 0L,
    val headline: String = "",
    val id: Long = 0L,
    val image: String = "",
    val related: String = "",
    val source: String = "",
    val summary: String = "",
    val url: String = ""
)

fun main() {
    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[*]").setAppName("Spark StockMarket")
    // initializarea contextului Spark
    val sparkContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
    val lines: JavaReceiverInputDStream<String> = sparkContext.socketTextStream("127.0.0.1", 9999)

    val formatJson = Json { ignoreUnknownKeys = true }

    val filtered_news = lines.map {
        try {
            println(">>> AM PRIMIT DE LA PYTHON: $it")
            Json.decodeFromString<News>(it)
        } catch (e: Exception) {
            println("eroare la deserializare")
            null
        }
    }
        .filter { it != null }
        .filter { it!!.source == "Yahoo"}
        .filter{ it!!.summary.length > 500 }

    filtered_news.foreachRDD { rdd ->
        val news_colectate = rdd.collect()

        if(news_colectate.isNotEmpty()) {
            news_colectate.forEach {
                println("URL: ${it!!.url}")
                val dataFormatata = Date(it.datetime * 1000)
                println("Data: $dataFormatata")
                println("Titlu: ${it.headline}")
                println()
            }
        }
    }

    sparkContext.start()
    sparkContext.awaitTermination()
}