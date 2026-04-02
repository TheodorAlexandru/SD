package com.sd.laborator.components

import com.sd.laborator.interfaces.OrderProcessor
import com.sd.laborator.model.Order
import com.sd.laborator.model.Waiter
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class RestaurantAppComponent {
    @Autowired
    private lateinit var chef: OrderProcessor

    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent
    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate()
    {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    fun sendMessage(msg: String)
    {
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(),
                                         connectionFactory.getRoutingKey(),
                                         msg)
    }

    @RabbitListener(queues = ["\${restaurantapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String)
    {
        val (_, waiterId, menu) = msg.split(":")

        val waiter = Waiter(waiterId)

        val order = Order(
            orderId = "ORD${System.currentTimeMillis()}",
            menuItem = menu.toInt(),
            waiter = waiter
        )

        val result = chef.processOrder(order)
        sendMessage("result~$result")

    }
}