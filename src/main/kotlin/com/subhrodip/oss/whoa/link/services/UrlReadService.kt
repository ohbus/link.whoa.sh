package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class UrlReadService(
    private val urlRepository: UrlRepository,
    private val analyticsService: AnalyticsService,
    private val urlCacheService: UrlCacheService,
) {
    @Timed(value = "whoa.service.urls.read.time", description = "Execution time for URL resolution logic")
    fun getOriginalUrl(
        shortCode: String,
        userAgent: String?,
        ipAddress: String?,
    ): String {
        // Try to get from cache/lookup first
        val urlDto = urlCacheService.getCachedUrl(shortCode)
        log.debug { "Resolved short code $shortCode to ${urlDto.originalUrl}" }

        // Use a proxy reference to avoid a redundant DB hit for the entity
        // We only need the ID to establish the relationship in trackAnalytics
        val urlEntity = urlRepository.getReferenceById(urlDto.id)

        analyticsService.trackAnalytics(urlEntity, userAgent, ipAddress)

        val originalUrl = urlDto.originalUrl
        if (!originalUrl.startsWith("http")) {
            return "https://$originalUrl"
        }
        return originalUrl
    }
}
