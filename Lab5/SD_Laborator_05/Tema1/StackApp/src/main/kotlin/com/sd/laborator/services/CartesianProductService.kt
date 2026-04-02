package com.sd.laborator.services

import com.sd.laborator.interfaces.CartesianProductOperation
import com.sd.laborator.model.ChainInput
import com.sd.laborator.model.Data
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStep
import io.github.damir.denis.tudor.spring.aop.chain.interfaces.Chainable
import org.springframework.stereotype.Service

@Service
@ChainStep(next = UnionService::class)
class CartesianProductService: CartesianProductOperation, Chainable<ChainInput, Data> {
    override fun executeOperation(A: Set<Int>, B: Set<Int>): Set<Pair<Int, Int>> {
        var result: MutableSet<Pair<Int, Int>> = mutableSetOf()
        A.forEach { a -> B.forEach { b -> result.add(Pair(a, b)) } }
        return result.toSet()
    }

    override fun proceed(input: ChainInput): Data {
        val AxB : Set<Pair<Int,Int>> = executeOperation(input.A, input.B)
        val BxB : Set<Pair<Int,Int>> = executeOperation(input.B, input.B)

        return Data(input.A, input.B, AxB, BxB)
    }
}