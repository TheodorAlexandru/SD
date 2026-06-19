package com.sd.laborator

import io.micronaut.http.client.annotation.Client
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post

@Client("http://localhost:8081")
interface NumerePrimeClient {

    @Post("/eratosteneQueue")
    fun trimiteCatreCoada(@Body date: Map<String, Any>)
}