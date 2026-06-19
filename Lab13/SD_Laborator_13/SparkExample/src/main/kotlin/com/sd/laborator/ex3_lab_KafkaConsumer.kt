package com.sd.laborator

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import scala.Tuple2

fun main(args: Array<String>) {
    // 1. Configurarea Spark Streaming cu interval de 5 secunde
    val sparkConf = SparkConf()
        .setAppName("KafkaTop15Words")
        .setMaster("local[*]")

    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(5))

    // 2. Configurarea parametrilor pentru Kafka [cite: 446-453]
    val kafkaParams = mapOf<String, Any>(
        "bootstrap.servers" to "localhost:9092",
        "key.deserializer" to StringDeserializer::class.java,
        "value.deserializer" to StringDeserializer::class.java,
        "group.id" to "spark_streaming_group",
        "auto.offset.reset" to "latest",
        "enable.auto.commit" to false
    )

    val topics = listOf("cuvinte_topic")

    // 3. Crearea fluxului direct din Kafka [cite: 463-469]
    val stream: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
        streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(topics, kafkaParams)
    )

    // 4. Extragerea cuvintelor din inregistrarile Kafka
    val words = stream.map { record -> record.value() }

    // 5. Generarea perechilor si numararea lor (Histograma)
    val wordCounts = words.mapToPair { word -> Tuple2(word, 1) }
        .reduceByKey { a, b -> a + b }

    // 6. Procesarea RDD-urilor individuale pentru a afla Top 15
    wordCounts.foreachRDD { rdd ->
        println("--- Top 15 cele mai frecvente cuvinte din batch ---")

        // Inversam perechea din (Cuvant, Numar) in (Numar, Cuvant) pentru a putea sorta dupa cheie
        val top15 = rdd.mapToPair { tuple -> Tuple2(tuple._2, tuple._1) }
            .sortByKey(false) // sortam descrescator
            .take(15)         // extragem primele 15

        // Afisam rezultatele
        top15.forEach { println("${it._2}: ${it._1}") }
    }

    // 7. Start aplicatie
    streamingContext.start()
    streamingContext.awaitTermination()
}