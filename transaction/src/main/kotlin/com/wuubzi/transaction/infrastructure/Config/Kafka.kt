package com.wuubzi.transaction.infrastructure.Config

import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonSerializer

@EnableKafka
@Configuration
class Kafka(
    @Value("\${spring.kafka.bootstrap-servers:localhost:9092}")
    private val bootstrapServers: String
) {

    companion object {
        private const val GROUP_ID = "transaction-service"
        const val TOPIC = "transactions.events"
    }

    // ==================== TOPIC ====================
    @Bean
    fun transactionsEventsTopic(): NewTopic =
        TopicBuilder.name(TOPIC)
            .partitions(3)
            .replicas(1)
            .build()

    // ==================== PRODUCER ====================
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

    // ==================== CONSUMER ====================
    @Bean
    fun transactionEventConsumerFactory(): ConsumerFactory<String, TransactionEvent> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,
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
    fun transactionEventKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> =
        ConcurrentKafkaListenerContainerFactory<String, TransactionEvent>().apply {
            setConsumerFactory(transactionEventConsumerFactory())
            setConcurrency(3)
            containerProperties.pollTimeout = 3000
        }
}