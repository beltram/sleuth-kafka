package com.github.jntakpe.sleuthkafka

import org.springframework.stereotype.Service
import reactor.core.publisher.toMono

@Service
class KafkaService(private val kafkaSender: DummyKafkaSender) {

    fun send() =
            "hello".toMono()
                    .flatMap { kafkaSender.send(it, Person("john", 40)) }
                    .map { Person("john", 40) }
                    .single()
}
