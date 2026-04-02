package com.sd.laborator.services
import com.sd.laborator.interfaces.OrderTakeOver
import com.sd.laborator.model.Waiter
import org.springframework.stereotype.Service


@Service
class WaiterService(private val idGen : GenerateIdService) : OrderTakeOver
{
    fun createWaiter(): Waiter
    {
        return Waiter(idGen.generateId("Waiter"))
    }

    override fun createOrder(waiter : Waiter, menuItem: Int): String
    {
        return "ORDER:${waiter.id}:$menuItem"
    }
}