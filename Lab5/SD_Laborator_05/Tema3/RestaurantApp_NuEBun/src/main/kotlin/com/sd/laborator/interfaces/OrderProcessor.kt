package com.sd.laborator.interfaces

import com.sd.laborator.model.Chef
import com.sd.laborator.model.Order

interface OrderProcessor {
    fun processOrder(order: Order): String
}