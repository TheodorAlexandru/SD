package com.sd.laborator.interfaces

import com.sd.laborator.model.Waiter

interface OrderTakeOver {
    fun createOrder(waiter : Waiter, menuItem: Int): String
}