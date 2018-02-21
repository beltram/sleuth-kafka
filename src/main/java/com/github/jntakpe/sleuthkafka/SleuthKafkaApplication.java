package com.github.jntakpe.sleuthkafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class SleuthKafkaApplication {

    @RestController
    @RequestMapping("/kafka")
    class KafkaResource {

        private AtomicInteger correlationId = new AtomicInteger();

        private final KafkaSender<String, String> kafkaSender;

        private final ObjectMapper objectMapper;

        KafkaResource(KafkaProperties kafkaProperties, ObjectMapper objectMapper) {
            Map<String, Object> properties = kafkaProperties.buildProducerProperties();
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            this.kafkaSender = KafkaSender.create(SenderOptions.create(properties));
            this.objectMapper = objectMapper;
        }

        @PostMapping
        public Mono<User> kafka(@RequestBody User user) throws JsonProcessingException {
            SenderRecord<String, String, Integer> record =
                    SenderRecord.create("some-topic", 0, null, user.getUsername(), objectMapper.writeValueAsString(user), correlationId.incrementAndGet());
            return kafkaSender.send(Mono.just(record)).map(i -> user).single();
        }
    }


	public static void main(String[] args) {
		SpringApplication.run(SleuthKafkaApplication.class, args);
	}
}
