package com.sd.laborator.cacheapp.persistence.entities

class CacheEntity (private var id: Int?, private var timestamp: String?, private var query: String?, private var result: String?){
    var gsId: Int?
        get(){
            return id
        }
        set(value){
            id = value
        }

    var gsTimestamp: String?
        get(){
            return timestamp
        }
        set(value){
            timestamp = value
        }

    var gsQuery: String?
        get(){
            return query
        }
        set(value){
            query = value
        }

    var gsResult: String?
        get(){
            return result
        }
        set(value){
            result = value
        }



}