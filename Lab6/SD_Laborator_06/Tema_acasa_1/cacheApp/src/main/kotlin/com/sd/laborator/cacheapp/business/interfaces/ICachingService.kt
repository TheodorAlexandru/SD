package com.sd.laborator.cacheapp.business.interfaces

interface ICachingService {

    fun createCacheTable()

    fun exists(query: String): String?
    fun addToCache(query: String, result: String)
}