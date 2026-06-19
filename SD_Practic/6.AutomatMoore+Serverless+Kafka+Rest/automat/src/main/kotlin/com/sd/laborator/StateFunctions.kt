package com.sd.laborator

import io.micronaut.function.FunctionBean
import java.util.function.Function

@FunctionBean("state00")
class State00Function : Function<StateRequest, StateResponse> {
    override fun apply(req: StateRequest): StateResponse {
        val next = if (req.input == 0) "10" else "01"
        return StateResponse(next, 0)
    }
}

@FunctionBean("state01")
class State01Function : Function<StateRequest, StateResponse> {
    override fun apply(req: StateRequest): StateResponse {
        val next = if (req.input == 0) "10" else "11"
        return StateResponse(next, 0)
    }
}

@FunctionBean("state10")
class State10Function : Function<StateRequest, StateResponse> {
    override fun apply(req: StateRequest): StateResponse {
        val next = if (req.input == 0) "10" else "01"
        return StateResponse(next, 0)
    }
}

@FunctionBean("state11")
class State11Function : Function<StateRequest, StateResponse> {
    override fun apply(req: StateRequest): StateResponse {
        val next = if (req.input == 0) "10" else "01"
        // Pentru starea 11, output-ul este 1
        return StateResponse(next, 1)
    }
}