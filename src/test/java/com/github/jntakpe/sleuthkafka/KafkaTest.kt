package com.github.jntakpe.sleuthkafka

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication
import org.springframework.security.core.userdetails.User
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.StepVerifier.create

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class KafkaTest(@Autowired private val kafkaService: KafkaService) {

    @Test
    fun testSend() {
        val mono = kafkaService.send().subscriberContext(withAuthentication(authToken))
        create(mono)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete()
    }

    companion object {
        private val user = User("dummy", "1234", listOf())
        private val authToken = TestingAuthenticationToken(user, user.password, user.authorities.toMutableList())
    }
}