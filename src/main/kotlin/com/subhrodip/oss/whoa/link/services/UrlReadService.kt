package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UrlReadService(
    private val urlRepository: UrlRepository,
    private val analyticsService: AnalyticsService,
    private val urlCacheService: UrlCacheService,
) {
    fun getOriginalUrl(
        shortCode: String,
        userAgent: String?,
        ipAddress: String?,
    ): String {
        // Try to get from cache/lookup first
        val urlDto = urlCacheService.getCachedUrl(shortCode)

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
