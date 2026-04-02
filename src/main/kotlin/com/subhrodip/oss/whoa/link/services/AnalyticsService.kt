package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
    private val urlRepository: UrlRepository,
    private val urlCacheService: UrlCacheService,
) {
    @Value("\${app.baseUrl:http://localhost:8844}")
    private lateinit var baseUrl: String

    @Async
    @Transactional
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
    }

    @Transactional(readOnly = true)
    fun getUrlAnalytics(shortCode: String): UrlAnalyticsResponse {
        val urlDto = urlCacheService.getCachedUrl(shortCode)
        val clicks = urlAnalyticsRepository.countByUrlEntityId(urlDto.id)
        return UrlAnalyticsResponse(
            originalUrl = urlDto.originalUrl,
            shortUrl = "$baseUrl/${urlDto.shortCode}",
            clicks = clicks,
        )
    }

    @Transactional(readOnly = true)
    fun getBulkAnalytics(shortCodes: List<String>): BulkAnalyticsResponse {
        val counts = urlAnalyticsRepository.countByShortCodes(shortCodes)
        val clickMap = counts.associate { it[0] as String to it[1] as Long }
        return BulkAnalyticsResponse(clicks = clickMap)
    }
}
