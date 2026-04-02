package com.sd.laborator.cacheapp.business.models

import com.sd.laborator.cacheapp.persistence.entities.CacheEntity
import java.text.SimpleDateFormat
import java.util.Date

class CacheModel (private var query: String?, private var result: String?){
    var gsQuery : String?
        get(){
            return query
        }
        set(value){
            query = value
        }

    var gsResult : String?
        get(){
            return result
        }
        set(value){
            result = value
        }

    fun toEntity(): CacheEntity {
        return CacheEntity(
            null,
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date()),
            gsQuery,
            gsResult
        )
    }

    companion object {
        fun fromEntity(entity: CacheEntity): CacheModel {
            return CacheModel(
                entity.gsQuery,
                entity.gsResult
            )
        }
    }
}