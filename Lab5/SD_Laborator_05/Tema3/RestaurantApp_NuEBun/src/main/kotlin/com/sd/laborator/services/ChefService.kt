package com.sd.laborator.services

import com.sd.laborator.interfaces.OrderProcessor
import com.sd.laborator.model.Chef
import com.sd.laborator.model.Order
import org.springframework.stereotype.Service

@Service
class ChefService(
    private val idGen: GenerateIdService) : OrderProcessor
{
    fun createChef(): Chef
    {
        return Chef(idGen.generateId("Chef"))
    }

    override fun processOrder(order: Order): String
    {
        val chef = createChef()

        Thread.sleep((1000..4000).random().toLong())

        return "Chef ${chef.id} prepared order ${order.menuItem} from ${order.waiter.id}"
    }
}