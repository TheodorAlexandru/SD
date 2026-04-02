package com.sd.laborator.cacheapp.presentation.config

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RabbitMqComponent {
    @Value("\${spring.rabbitmq.host}")
    private lateinit var host: String

    @Value("\${spring.rabbitmq.port}")
    private var port: Int = 0

    @Value("\${spring.rabbitmq.username}")
    private lateinit var username: String

    @Value("\${spring.rabbitmq.password}")
    private lateinit var password: String

    @Value("\${spring.rabbitmq.exchange}")
    private lateinit var exchange: String

    @Value("\${cacheapp.rabbitmq.routingkey.responses}")
    private lateinit var routingKeyResponse: String

    fun getExchange(): String = this.exchange

    fun getRoutingKeyResponse(): String = this.routingKeyResponse

    @Bean
    private fun connectionFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory(this.host)
        connectionFactory.username = this.username
        connectionFactory.setPassword(this.password)
        connectionFactory.port = this.port
        return connectionFactory
    }

    @Bean
    fun rabbitTemplate(): RabbitTemplate = RabbitTemplate(connectionFactory())
}