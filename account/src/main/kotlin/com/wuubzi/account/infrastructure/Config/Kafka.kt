package com.wuubzi.account.infrastructure.Config


import com.wuubzi.account.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.account.application.DTOS.Events.UserRequestEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer

@Configuration
class Kafka {

    companion object {
        private const val BOOTSTRAP_SERVERS = "localhost:9092"
        private const val GROUP_ID = "account-service"
    }

    // ==================== PRODUCER ====================
    @Bean
    fun producerConfigs(): Map<String, Any> = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java
    )

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> =
        DefaultKafkaProducerFactory(producerConfigs())

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> =
        KafkaTemplate(producerFactory())

    // ==================== CONSUMER: USER CREATED ====================
    @Bean
    fun userCreatedConsumerFactory(): ConsumerFactory<String, UserRequestEvent> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
            ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS to false,
            JacksonJsonDeserializer.VALUE_DEFAULT_TYPE to UserRequestEvent::class.java.name,
            JacksonJsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            JacksonJsonDeserializer(UserRequestEvent::class.java)
        )
    }

    @Bean
    fun userCreatedKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, UserRequestEvent> {
        return ConcurrentKafkaListenerContainerFactory<String, UserRequestEvent>().apply {
            setConsumerFactory(userCreatedConsumerFactory())
            setConcurrency(3)
            containerProperties.pollTimeout = 3000
        }
    }

    // ==================== CONSUMER: USER DELETED ====================
    @Bean
    fun userDeletedConsumerFactory(): ConsumerFactory<String, UserDeletedEvent> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
            ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS to false,
            JacksonJsonDeserializer.VALUE_DEFAULT_TYPE to UserDeletedEvent::class.java.name,
            JacksonJsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            JacksonJsonDeserializer(UserDeletedEvent::class.java)
        )
    }

    @Bean
    fun userDeletedKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent> {
        return ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent>().apply {
            setConsumerFactory(userDeletedConsumerFactory())  // ← Usa el setter
            setConcurrency(3)
            containerProperties.pollTimeout = 3000
        }
    }
}