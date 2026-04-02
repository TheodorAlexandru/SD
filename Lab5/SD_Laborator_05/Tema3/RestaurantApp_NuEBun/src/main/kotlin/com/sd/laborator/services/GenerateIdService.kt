package com.sd.laborator.services

import com.sd.laborator.interfaces.IdGenerator
import org.springframework.stereotype.Service

@Service
class GenerateIdService : IdGenerator {
    override fun generateId(prefix: String): String {
        val suffix = (1..6).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString("")
        return "$prefix$suffix"
    }
}