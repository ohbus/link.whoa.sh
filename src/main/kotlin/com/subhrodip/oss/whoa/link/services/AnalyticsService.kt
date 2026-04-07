package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset

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
        urlId: Long,
        shortCode: String,
        userAgent: String?,
        ipAddress: String?,
    ) {
        val urlEntity = urlRepository.getReferenceById(urlId)
        val analytics =
            UrlAnalyticsEntity(
                urlEntity = urlEntity,
                userAgent = userAgent,
                ipAddress = ipAddress,
            )
        urlAnalyticsRepository.save(analytics)
        urlRepository.incrementClickCount(urlId)
        urlCacheService.evictUrlCache(shortCode)
        globalCounterService.incrementRealTime()
        log.debug { "Tracked click for short code: $shortCode" }
    }

    @Transactional(readOnly = true)
    @Timed(value = "whoa.service.analytics.single.time", description = "Execution time for single URL analytics lookup")
    fun getUrlAnalytics(shortCode: String): UrlAnalyticsResponse {
        val urlDto = urlCacheService.getCachedUrl(shortCode)

        log.debug { "Retrieved analytics for code: $shortCode (Clicks: ${urlDto.totalClicks})" }
        return UrlAnalyticsResponse(
            originalUrl = urlDto.originalUrl,
            shortUrl = "$baseUrl/${urlDto.shortCode}",
            clicks = urlDto.totalClicks,
            createdAt = urlDto.createdAt,
        )
    }

    @Transactional(readOnly = true)
    @Timed(value = "whoa.service.analytics.bulk.time", description = "Execution time for bulk analytics delta sync (No-Join)")
    fun getBulkAnalytics(
        currentCounts: Map<String, Long>,
        lastSyncedAt: Long?,
    ): BulkAnalyticsResponse {
        val requestedCodes = currentCounts.keys.toList()
        if (requestedCodes.isEmpty()) {
            return BulkAnalyticsResponse(emptyMap(), Instant.now().toEpochMilli())
        }

        // 1. Resolve codes to IDs via Cache (No DB hit for lookup)
        val codeToIdMap = requestedCodes.associateWith { urlCacheService.getCachedUrl(it).id }
        val requestedIds = codeToIdMap.values.toList()
        val idToCodeMap = codeToIdMap.entries.associate { it.value to it.key }

        // 2. Identify Deltas by ID (No JOIN)
        val idsToSync =
            if (lastSyncedAt != null) {
                val since = Instant.ofEpochMilli(lastSyncedAt).atOffset(ZoneOffset.UTC)
                val changedIds = urlAnalyticsRepository.findIdsWithActivitySince(requestedIds, since)
                log.debug { "Delta sync: ${changedIds.size} / ${requestedIds.size} links have new activity since $since" }
                changedIds
            } else {
                requestedIds
            }

        // 3. Bulk Count by ID (No JOIN)
        val clickMap =
            if (idsToSync.isNotEmpty()) {
                val projectionResults = urlAnalyticsRepository.countByUrlIds(idsToSync)
                projectionResults.associate { idToCodeMap[it.urlId]!! to it.totalClicks }
            } else {
                emptyMap()
            }

        return BulkAnalyticsResponse(
            clicks = clickMap,
            serverTimestamp = Instant.now().toEpochMilli(),
        )
    }
}
