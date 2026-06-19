package com.sd.laborator

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ButtonRepositoryInterface: CrudRepository<ButtonRepository, Int> {
    fun findByNumeButon(numeButon: String): ButtonRepository?
}