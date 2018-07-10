package com.github.jntakpe.sleuthkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SleuthKafkaApplication

fun main(args: Array<String>) {
    runApplication<SleuthKafkaApplication>(*args)
}
