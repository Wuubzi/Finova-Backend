package com.wuubzi.notification.Config

import com.wuubzi.notification.DTOS.Events.RecoverPasswordRequest
import com.wuubzi.notification.DTOS.Events.TransactionEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer

@EnableKafka
@Configuration
class Kafka {

    companion object {
        private const val BOOTSTRAP_SERVERS = "localhost:9092"
    }

    // ==================== CONSUMER: RECOVER PASSWORD ====================
    @Bean
    fun recoverPasswordConsumerFactory(): ConsumerFactory<String, RecoverPasswordRequest> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
            ConsumerConfig.GROUP_ID_CONFIG to "notification-service-v2",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS to false,
            JacksonJsonDeserializer.VALUE_DEFAULT_TYPE to RecoverPasswordRequest::class.java.name,
            JacksonJsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun recoverPasswordKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, RecoverPasswordRequest> {
        return ConcurrentKafkaListenerContainerFactory<String, RecoverPasswordRequest>().apply {
            setConsumerFactory(recoverPasswordConsumerFactory())
            setConcurrency(3)
            containerProperties.pollTimeout = 3000
        }
    }

    // ==================== CONSUMER: TRANSACTION EVENTS ====================
    @Bean
    fun transactionEventConsumerFactory(): ConsumerFactory<String, TransactionEvent> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
            ConsumerConfig.GROUP_ID_CONFIG to "notification-service",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS to false,
            JacksonJsonDeserializer.VALUE_DEFAULT_TYPE to TransactionEvent::class.java.name,
            JacksonJsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun transactionEventKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> {
        return ConcurrentKafkaListenerContainerFactory<String, TransactionEvent>().apply {
            setConsumerFactory(transactionEventConsumerFactory())
            setConcurrency(3)
            containerProperties.pollTimeout = 3000
        }
    }
}