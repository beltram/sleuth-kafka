package com.github.jntakpe.sleuthkafka

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult

@Repository
class DummyKafkaSender(private val kafkaProducer: KafkaProducer) {

    fun send(topic: String, person: Person): Mono<SenderResult<Int>> {
        return kafkaProducer.send(topic, person)
    }
}