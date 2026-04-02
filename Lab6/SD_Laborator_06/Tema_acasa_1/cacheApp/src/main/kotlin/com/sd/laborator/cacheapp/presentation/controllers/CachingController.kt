package com.sd.laborator.cacheapp.presentation.controllers

import com.sd.laborator.cacheapp.business.services.CachingService
import com.sd.laborator.cacheapp.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CachingController {
    @Autowired
    private lateinit var _cachingService: CachingService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate(){
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${cacheapp.rabbitmq.requestqueue}"])
    fun handleCacheRequest(msg: String){
        val pieces = msg.split("~")
        if(pieces.size > 1){
            val query = pieces[1]
            val result = _cachingService.exists(query)

            if(result != null)
                sendResponse("HIT~$query~$result")
            else
                sendResponse("MISS~$query")
        }


    }

    @RabbitListener(queues = ["\${cacheapp.rabbitmq.updatequeue}"])
    fun handleCacheUpdate(msg: String){
        val pieces = msg.split("~")
        if (pieces.size >= 3){
            val query = pieces[1]
            val result = pieces[2]
            _cachingService.addToCache(query, result)
            println("Cache updated pentru query: $query")
        }
    }

    fun sendResponse(msg: String){
        println("Message: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(),_rabbitMqComponent.getRoutingKeyResponse(), msg)
    }
}