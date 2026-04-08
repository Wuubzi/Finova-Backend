package com.wuubzi.auth.infrastructure.Config

import com.wuubzi.auth.application.DTOS.Events.UserDeletedEvent
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonSerializer

@Configuration
class Kafka(
    @Value("\${spring.kafka.bootstrap-servers:localhost:9092}")
    private val bootstrapServers: String
) {

    companion object {
        private const val GROUP_ID = "auth-service"
        private const val TOPIC_PARTITIONS = 3
        private const val TOPIC_REPLICAS = 1
    }

    @Bean
    fun producerConfigs(): Map<String, Any> = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JacksonJsonSerializer::class.java
    )

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> =
        DefaultKafkaProducerFactory(producerConfigs())

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> =
        KafkaTemplate(producerFactory())

    @Bean
    fun userCreatedTopic(): NewTopic =
        createTopic("user-created")

    @Bean
    fun recoverPasswordTopic(): NewTopic =
        createTopic("recover-password")

    @Bean
    fun kafkaListenerContainerFactory():
            KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Any>> {
        return ConcurrentKafkaListenerContainerFactory<String, Any>().apply {
            setConsumerFactory(consumerFactory())
            setConcurrency(3)
            containerProperties.pollTimeout = 3000
        }
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> =
        DefaultKafkaConsumerFactory(consumerConfigs())

    @Bean
    fun consumerConfigs(): Map<String, Any> = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS to false,
        JacksonJsonDeserializer.VALUE_DEFAULT_TYPE to UserDeletedEvent::class.java,
        JacksonJsonDeserializer.TRUSTED_PACKAGES to "*"
    )

    private fun createTopic(name: String): NewTopic =
        TopicBuilder.name(name)
            .partitions(TOPIC_PARTITIONS)
            .replicas(TOPIC_REPLICAS)
            .build()
}