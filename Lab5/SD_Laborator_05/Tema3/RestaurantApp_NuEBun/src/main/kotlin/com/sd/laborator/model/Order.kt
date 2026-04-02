package com.sd.laborator.model

data class Order(
    val orderId: String,
    val menuItem: Int,
    val waiter: Waiter
)