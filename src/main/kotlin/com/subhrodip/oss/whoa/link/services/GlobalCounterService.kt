package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

private val log = KotlinLogging.logger {}

@Service
class GlobalCounterService(
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
) {
    private val globalClicks = AtomicLong(0)

    @PostConstruct
    fun init() {
        refreshFromDatabase()
        log.info { "Initialized Global Counter with authoritative database count: ${globalClicks.get()}" }
    }

    /**
     * Highly optimized in-memory retrieval.
     */
    fun getTotalClicks(): Long = globalClicks.get()

    /**
     * Periodically synchronizes the in-memory counter with the database.
     * This ensures the counter remains authoritative even if multiple instances exist
     * or if the database is modified externally.
     */
    @Timed(value = "whoa.counter.refresh.time", description = "Execution time for DB-to-Memory sync")
    @Scheduled(fixedDelayString = "\${app.analytics.refresh-interval-ms:10000}")
    fun refreshFromDatabase() {
        val latestAuthoritativeCount = urlAnalyticsRepository.countAllClicks()
        val previousCount = globalClicks.getAndSet(latestAuthoritativeCount)

        if (latestAuthoritativeCount != previousCount) {
            log.debug { "Global counter synced with DB: $previousCount -> $latestAuthoritativeCount" }
        }
    }

    /**
     * Increment the local memory state immediately for instant feedback.
     * The next scheduled refresh will confirm this against the DB.
     */
    fun incrementRealTime() {
        globalClicks.incrementAndGet()
    }
}
