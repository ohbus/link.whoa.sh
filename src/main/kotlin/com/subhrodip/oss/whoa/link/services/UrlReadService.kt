package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.dto.PagedUrlsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class UrlReadService(
    private val urlRepository: UrlRepository,
    private val analyticsService: AnalyticsService,
    private val urlCacheService: UrlCacheService,
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
) {
    @Value("\${app.baseUrl:http://localhost:8844}")
    private lateinit var baseUrl: String

    @Timed(value = "whoa.service.urls.read.time", description = "Execution time for URL resolution logic")
    fun getOriginalUrl(
        shortCode: String,
        userAgent: String?,
        ipAddress: String?,
    ): String {
        val urlDto = urlCacheService.getCachedUrl(shortCode)
        log.debug { "Resolved short code $shortCode to ${urlDto.originalUrl}" }

        val urlEntity = urlRepository.getReferenceById(urlDto.id)
        analyticsService.trackAnalytics(urlEntity, userAgent, ipAddress)

        val originalUrl = urlDto.originalUrl
        if (!originalUrl.startsWith("http")) {
            return "https://$originalUrl"
        }
        return originalUrl
    }

    @Timed(value = "whoa.service.urls.paged.time", description = "Execution time for server-side Keyset pagination")
    fun getPagedUrls(
        cursor: Long?,
        limit: Int,
    ): PagedUrlsResponse {
        val fetchLimit = limit + 1
        val pageable = PageRequest.of(0, fetchLimit)

        val entities =
            if (cursor != null) {
                val cursorDateTime = Instant.ofEpochMilli(cursor).atOffset(ZoneOffset.UTC)
                urlRepository.findByCreatedAtBefore(cursorDateTime, pageable)
            } else {
                urlRepository.findLatest(pageable)
            }

        val hasMore = entities.size > limit
        val linksToReturn = if (hasMore) entities.take(limit) else entities

        val codes = linksToReturn.map { it.shortCode }
        val counts =
            if (codes.isNotEmpty()) {
                urlAnalyticsRepository.countByShortCodes(codes)
                    .associate { it.shortCode to it.totalClicks }
            } else {
                emptyMap()
            }

        val results =
            linksToReturn.map { entity ->
                UrlAnalyticsResponse(
                    originalUrl = entity.originalUrl,
                    shortUrl = "$baseUrl/${entity.shortCode}",
                    clicks = counts[entity.shortCode] ?: 0L,
                    createdAt = entity.createdAt
                )
            }

        val nextCursor = if (hasMore) linksToReturn.last().createdAt.toInstant().toEpochMilli() else null

        return PagedUrlsResponse(
            links = results,
            nextCursor = nextCursor,
            hasMore = hasMore,
        )
    }
}
