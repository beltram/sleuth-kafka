package com.github.jntakpe.sleuthkafka

import brave.Tracer
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jntakpe.sleuthkafka.ProfilesConstants.NOT_TEST_PROFILE
import com.github.jntakpe.sleuthkafka.ProfilesConstants.TEST_PROFILE
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.internals.DefaultKafkaSender

@Configuration
@AutoConfigureAfter(JacksonAutoConfiguration::class)
@EnableConfigurationProperties(KafkaProperties::class)
class KafkaConfiguration {

    @Bean
    fun kafkaProducer(props: KafkaProperties, mapper: ObjectMapper, tracer: Tracer) = KafkaProducer(props, mapper, tracer)

    @Bean
    @Profile(NOT_TEST_PROFILE)
    fun kafkaSender(kafkaProperties: KafkaProperties): KafkaSender<String, String> = KafkaSender.create(senderOptions(kafkaProperties))

    @Bean
    @Profile(TEST_PROFILE)
    fun testKafkaSender(kafkaProperties: KafkaProperties): KafkaSender<String, String> =
            DefaultKafkaSender(TestProducerFactory(mockProducer()), senderOptions(kafkaProperties))

    @Bean
    @Profile(TEST_PROFILE)
    fun mockProducer(): MockProducer<String, String> = MockProducer(true, StringSerializer(), StringSerializer())


    private fun senderOptions(kafkaProperties: KafkaProperties): SenderOptions<String, String> {
        val properties = kafkaProperties.buildProducerProperties()
        properties[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return SenderOptions.create<String, String>(properties)
    }
}
