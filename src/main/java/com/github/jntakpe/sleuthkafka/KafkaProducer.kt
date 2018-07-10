package com.github.jntakpe.sleuthkafka

import brave.Tracer
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import java.util.concurrent.atomic.AtomicInteger

class KafkaProducer(kafkaProperties: KafkaProperties, private val objectMapper: ObjectMapper, private val tracer: Tracer) {

    private val kafkaSender: KafkaSender<String, String>

    init {
        val properties = kafkaProperties.buildProducerProperties()
        properties[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        kafkaSender = KafkaSender.create(SenderOptions.create(properties))
    }

    internal fun send(topic: String, payload: Any): Mono<SenderResult<Int>> {
        val span = tracer.currentSpan()
        val producerRecord = ProducerRecord<String, String>(topic, serialize(payload))
        val record = SenderRecord.create(producerRecord, correlationId.incrementAndGet())
        return kafkaSender.send(Mono.just(record))
                .doOnSubscribe { logger.info("Sending ...") }
                .doOnNext { logger.info("... succeeded") }
                .doOnError { logger.info("... failed") }
                .single()
    }

    private fun serialize(payload: Any): String {
        try {
            return objectMapper.writeValueAsString(payload)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Payload has to be serializable")
        }
    }

    companion object {
        private val logger = getLogger(KafkaProducer::class.java)
        private val correlationId = AtomicInteger()
    }
}