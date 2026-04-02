package com.sd.laborator.services

import com.sd.laborator.model.ChainInput
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStart
import io.github.damir.denis.tudor.spring.aop.chain.registry.ChainResult
import org.springframework.stereotype.Service

@Service
open class ChainStartService{
    @ChainStart(node = CartesianProductService::class)
    open fun process(input: ChainInput): ChainResult<String> = ChainResult.Pending
}