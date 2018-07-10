package com.github.jntakpe.sleuthkafka

import org.apache.kafka.clients.producer.MockProducer
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.internals.ProducerFactory

class TestProducerFactory(private val mockProducer: MockProducer<String, String>) : ProducerFactory() {

    override fun <K : Any, V : Any> createProducer(senderOptions: SenderOptions<K, V>): MockProducer<K, V> = mockProducer as MockProducer<K, V>
}