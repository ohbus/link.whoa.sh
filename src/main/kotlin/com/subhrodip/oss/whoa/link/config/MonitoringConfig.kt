package com.subhrodip.oss.whoa.link.config

import io.micrometer.core.aop.CountedAspect
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class MonitoringConfig {
    /**
     * Enables the @Timed annotation support for methods.
     */
    @Bean
    fun timedAspect(registry: MeterRegistry): TimedAspect = TimedAspect(registry)

    /**
     * Enables the @Counted annotation support for methods.
     */
    @Bean
    fun countedAspect(registry: MeterRegistry): CountedAspect = CountedAspect(registry)
}
