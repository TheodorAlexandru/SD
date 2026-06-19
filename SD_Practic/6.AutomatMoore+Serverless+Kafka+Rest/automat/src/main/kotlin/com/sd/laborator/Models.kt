package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
data class StateRequest(var input: Int = 0)

@Introspected
data class StateResponse(var nextState: String = "", var output: Int = 0)