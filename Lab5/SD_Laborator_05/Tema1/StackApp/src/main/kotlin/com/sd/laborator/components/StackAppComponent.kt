package com.sd.laborator.components


import com.sd.laborator.interfaces.PrimeNumberGenerator
import com.sd.laborator.model.ChainInput
import com.sd.laborator.model.Stack
import com.sd.laborator.services.ChainStartService
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StackAppComponent (
    private val triggerChain: ChainStartService
){
    private var A: Stack? = null
    private var B: Stack? = null

    @Autowired
    private lateinit var primeGenerator: PrimeNumberGenerator
    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${stackapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        // the result: 114,101,103,101,110,101,114,97,116,101,95,65 --> needs processing
        val processed_msg = try {
            (msg.split(",").map { it.toInt().toChar() }).joinToString(separator = "")
        } catch (e: NumberFormatException) {
            msg
        }
        var result: String? = when(processed_msg) {
            "compute" -> computeExpression()
            "regenerate_A" -> regenerateA()
            "regenerate_B" -> regenerateB()
            else -> {
                println("Mesaj necunoscut: $processed_msg")
                null
            }
        }
        println("result: ")
        println(result)
        if (result != null) sendMessage(result)
    }

    fun sendMessage(msg: String) {
        println("message: ")
        println(msg)
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(),
            connectionFactory.getRoutingKey(),
            msg)
    }

    private fun generateStack(count: Int): Stack? {
        if (count < 1)
            return null
        var x: MutableSet<Int> = mutableSetOf()
        while (x.count() < count)
            x.add(primeGenerator.generatePrimeNumber())
        return Stack(x)
    }

    private fun computeExpression(): String {
        if (A == null)
            A = generateStack(20)
        if (B == null)
            B = generateStack(20)
        if (A!!.data.count() == B!!.data.count()) {
            val input = ChainInput(A!!.data, B!!.data)

            val response = triggerChain.process(input)
            return response.result
        }
        return "compute~" + "Error: A.count() != B.count()"
    }

    private fun regenerateA(): String {
        A = generateStack(20)
        return "A~" + A?.data.toString()
    }

    private fun regenerateB(): String {
        B = generateStack(20)
        return "B~" + B?.data.toString()
    }
}
