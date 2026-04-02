package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Counted
import io.micrometer.core.annotation.Timed
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

private val log = KotlinLogging.logger {}

@Service
class GlobalCounterService(
    private val urlAnalyticsRepository: UrlAnalyticsRepository
) {
    private val globalClicks = AtomicLong(0)

    @Value("\${app.analytics.simulation.min-increment:1}")
    private var minIncrement: Int = 1

    @Value("\${app.analytics.simulation.max-increment:5}")
    private var maxIncrement: Int = 5

    @PostConstruct
    fun init() {
        val initialCount = urlAnalyticsRepository.countAllClicks()
        globalClicks.set(initialCount)
        log.info { "Initialized Global Counter with authoritative database count: $initialCount" }
    }

    /**
     * Highly optimized in-memory retrieval.
     */
    fun getTotalClicks(): Long = globalClicks.get()

    /**
     * Periodically increments the counter to simulate real-time activity (Dopamine effect).
     * Defaults to every 10 seconds.
     */
    @Timed(value = "whoa.simulation.traffic.time", description = "Execution time for traffic simulation task")
    @Counted(value = "whoa.simulation.traffic.count", description = "Number of traffic simulation cycles")
    @Scheduled(fixedDelayString = "\${app.analytics.simulation.interval-ms:10000}")
    fun simulateTraffic() {
        val increment = Random.nextLong(minIncrement.toLong(), maxIncrement.toLong() + 1)
        val newValue = globalClicks.addAndGet(increment)
        log.debug { "Simulated traffic increment: +$increment. New global total: $newValue" }
    }
    
    /**
     * Authority update when a real redirect happens.
     */
    fun incrementRealTime() {
        globalClicks.incrementAndGet()
    }
}
