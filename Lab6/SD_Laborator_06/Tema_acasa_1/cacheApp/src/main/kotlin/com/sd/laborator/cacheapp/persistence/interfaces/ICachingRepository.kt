package com.sd.laborator.cacheapp.persistence.interfaces

import com.sd.laborator.cacheapp.persistence.entities.CacheEntity

interface ICachingRepository {

    fun createCacheTable()

    fun getByQuery(query: String): CacheEntity?
    fun add(item: CacheEntity)
    fun update(item: CacheEntity)
}