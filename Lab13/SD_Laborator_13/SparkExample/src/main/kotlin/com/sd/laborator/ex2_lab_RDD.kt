package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import scala.Tuple2

fun main(args: Array<String>) {
    // configurarea Spark
    // local[*,6] -> ruleaza pe atatea worker threads cate procesoare logice are masina si pana la 6 maxFailures
    val sparkConf = SparkConf().setMaster("local[*,6]").setAppName("Spark Example")
    // initializarea contextului Spark
    val sparkContext = JavaSparkContext(sparkConf)

    val lines = sparkContext.textFile("src/main/resources/ebook.txt")

    val histogram = lines
        // 1) spargem fiecare linie in caractere individuale
        .flatMap { line -> line.toList().iterator() }

        // 2) pastram doar literele a-z si A-Z
        .filter { char -> char.isLetter() }

        // 3) convertim la lowercase ca 'A' si 'a' sa fie acelasi caracter
        .map { char -> char.lowercaseChar() }

        // 4) transformam fiecare caracter intr-o pereche (caracter, 1)
        .mapToPair { char -> Tuple2(char, 1) }

        // 5) adunam toate valorile cu aceeasi cheie
        // ex: ('a', 1), ('a', 1), ('a', 1) -> ('a', 3)
        .reduceByKey { a, b -> a + b }

    histogram.foreach { tuple2 ->
        println("${tuple2._1} -> ${tuple2._2} ")
    }



    // oprirea contextului Spark
    sparkContext.stop()
}