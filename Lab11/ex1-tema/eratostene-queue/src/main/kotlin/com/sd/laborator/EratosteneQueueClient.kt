package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8082")
interface EratosteneQueueClient {

    @Post("/eratostene")
    fun trimiteCatreEratostene(@Body date: Map<String, Any>)
}