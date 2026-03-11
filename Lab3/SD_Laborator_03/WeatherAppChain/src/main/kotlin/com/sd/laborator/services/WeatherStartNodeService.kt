package com.sd.laborator.services

import com.sd.laborator.pojo.Weather_I_O
import io.github.damir.denis.tudor.spring.aop.chain.annotation.ChainStart
import io.github.damir.denis.tudor.spring.aop.chain.registry.ChainResult
import org.springframework.stereotype.Service

@Service
open class WeatherStartNodeService {
    @ChainStart(node = BlackzoneService::class)
    open fun process(input : Weather_I_O) : ChainResult<String> = ChainResult.Pending
}