package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Person

interface ICacheService {
    fun checkResource(uriString: String): Boolean
    fun getResource(uriString: String): List<Person?>
    fun addResource(data: Pair<String, List<Person?>>)
}