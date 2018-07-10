package com.github.jntakpe.sleuthkafka

import brave.Tracing
import brave.opentracing.BraveTracer
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(TraceAutoConfiguration::class)
class TracingConfiguration {

    @Bean
    fun openTracingTracer(tracing: Tracing): BraveTracer = BraveTracer.create(tracing)
}
