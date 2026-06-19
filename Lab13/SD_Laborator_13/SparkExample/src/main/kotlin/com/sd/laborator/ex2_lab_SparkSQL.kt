package com.sd.laborator

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.*

fun main(args: Array<String>) {
    // configurarea si crearea sesiunii Spark SQL
    val sparkSession = SparkSession.builder()
        .appName("Java Spark SQL example")
        .config("spark.master", "local")
        .orCreate

    // initializarea unui DataFrame prin citirea unui json
    val df = sparkSession.sqlContext().read().textFile("src/main/resources/ebook.txt")

    df
        // 1) spargem fiecare linie in caractere individuale
        // split("") imparte stringul caracter cu caracter
        .select(explode(split(col("value"), "")).alias("caracter"))

        // 2) pastram doar literele a-z si A-Z
        .filter(col("caracter").rlike("[a-zA-Z]"))

        // 3) convertim la lowercase
        .select(lower(col("caracter")).alias("caracter"))

        // 4) grupam dupa caracter si numaram
        .groupBy("caracter")
        .count()

        // 5) afisam
        .show(26)

}