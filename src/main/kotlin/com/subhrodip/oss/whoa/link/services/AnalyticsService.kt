package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class AnalyticsService(
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
    private val urlRepository: UrlRepository,
    private val urlCacheService: UrlCacheService,
    private val globalCounterService: GlobalCounterService,
) {
    @Value("\${app.baseUrl:http://localhost:8844}")
    private lateinit var baseUrl: String

    @Async
    @Transactional
    @Timed(value = "whoa.service.analytics.track.time", description = "Time taken to asynchronously save a click")
    fun trackAnalytics(
        urlEntity: UrlEntity,
        userAgent: String?,
        ipAddress: String?,
    ) {
        val analytics =
            UrlAnalyticsEntity(
                urlEntity = urlEntity,
                userAgent = userAgent,
                ipAddress = ipAddress,
            )
        urlAnalyticsRepository.save(analytics)
        globalCounterService.incrementRealTime()
        log.debug { "Tracked click for short code: ${urlEntity.shortCode}" }
    }

    @Transactional(readOnly = true)
    @Timed(value = "whoa.service.analytics.single.time", description = "Execution time for single URL analytics lookup")
    fun getUrlAnalytics(shortCode: String): UrlAnalyticsResponse {
        val urlDto = urlCacheService.getCachedUrl(shortCode)
        val clicks = urlAnalyticsRepository.countByUrlEntityId(urlDto.id)
        log.debug { "Retrieved analytics for code: $shortCode (Clicks: $clicks)" }
        return UrlAnalyticsResponse(
            originalUrl = urlDto.originalUrl,
            shortUrl = "$baseUrl/${urlDto.shortCode}",
            clicks = clicks,
        )
    }

    @Transactional(readOnly = true)
    @Timed(value = "whoa.service.analytics.bulk.time", description = "Execution time for bulk analytics database aggregation")
    fun getBulkAnalytics(currentCounts: Map<String, Long>): BulkAnalyticsResponse {
        val shortCodes = currentCounts.keys.toList()
        if (shortCodes.isEmpty()) return BulkAnalyticsResponse(emptyMap())

        val counts = urlAnalyticsRepository.countByShortCodes(shortCodes)
        val clickMap = counts.associate { it[0] as String to it[1] as Long }

        log.debug { "Retrieved bulk analytics for ${shortCodes.size} codes" }
        return BulkAnalyticsResponse(clicks = clickMap)
    }
}
