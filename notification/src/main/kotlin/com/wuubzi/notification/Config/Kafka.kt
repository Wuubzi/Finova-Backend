package com.wuubzi.notification.Config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JacksonJsonSerializer

@EnableKafka
@Configuration
class Kafka {
    @Bean
    fun kafkaListenerContainerFactory():
            KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Int, String>> {

        val factory = ConcurrentKafkaListenerContainerFactory<Int, String>()
        factory.setConsumerFactory(consumerFactory())
        factory.setConcurrency(3)
        factory.containerProperties.pollTimeout = 3000

        return factory
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<Int, String> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    fun consumerConfigs(): Map<String, Any> {

        val props = HashMap<String, Any>()

        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "notification-service"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JacksonJsonSerializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return props
    }
}