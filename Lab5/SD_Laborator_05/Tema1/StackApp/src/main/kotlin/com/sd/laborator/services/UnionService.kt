package com.sd.laborator.services

import com.sd.laborator.interfaces.CartesianProductOperation
import com.sd.laborator.interfaces.UnionOperation
import com.sd.laborator.model.Data
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStep
import io.github.damir.denis.tudor.spring.aop.chain.interfaces.Chainable
import org.springframework.stereotype.Service

@Service
@ChainStep
class UnionService: UnionOperation, Chainable<Data, String>{
    override fun executeOperation(A: Set<Pair<Int, Int>>, B: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return A union B
    }

    override fun proceed(input: Data): String {
        val unionResult = executeOperation(input.AxB, input.BxB)

        return "compute~{\"A\": \"${input.A}\", \"B\": \"${input.B}\", \"result\": \"$unionResult\"}"
    }

}