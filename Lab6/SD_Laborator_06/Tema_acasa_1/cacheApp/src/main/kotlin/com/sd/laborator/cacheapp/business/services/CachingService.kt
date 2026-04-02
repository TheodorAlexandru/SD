package com.sd.laborator.cacheapp.business.services

import com.sd.laborator.cacheapp.business.interfaces.ICachingService
import com.sd.laborator.cacheapp.business.models.CacheModel
import com.sd.laborator.cacheapp.persistence.interfaces.ICachingRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date
import java.text.SimpleDateFormat
import kotlin.math.abs

@Service
class CachingService : ICachingService{
    @Autowired
    private lateinit var _cachingRepo: ICachingRepository

    @PostConstruct
    override fun createCacheTable(){
        _cachingRepo.createCacheTable()
    }

    override fun addToCache(query: String, result: String) {
        val model = CacheModel(query, result)
        val entity = model.toEntity()

        val existingQuery = _cachingRepo.getByQuery(query)

        if (existingQuery == null) {
            //query-ul nu se afla deja in tabela cache
            _cachingRepo.add(entity)
        }
        else {
            existingQuery.gsTimestamp = entity.gsTimestamp
            existingQuery.gsResult = entity.gsResult
            _cachingRepo.update(existingQuery)
        }

    }

    override fun exists(query: String): String? {
        val entity = _cachingRepo.getByQuery(query) ?: return null // in caz de MISS - nu exista in db

        val cacheDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(entity.gsTimestamp)
        val nowDate = Date()

        val diffDate = abs(nowDate.time - cacheDate.time) //milisecunde
        val diffHours = diffDate / (360.0 * 1000.0)

        return if (diffHours < 1.0) {
            entity.gsResult // HIT - e de mai putin de o ora in db
        }
        else{
            null // MISS - e prea vechi
        }
    }
}