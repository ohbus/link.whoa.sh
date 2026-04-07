package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class UrlCacheService(
    private val urlRepository: UrlRepository,
) {
    @Cacheable(value = ["urls"], key = "'url:' + #shortCode", unless = "#result == null")
    fun getCachedUrl(shortCode: String): UrlDto {
        log.trace { "Fetching short code $shortCode from database (cache miss)" }
        val entity =
            urlRepository.findByShortCode(shortCode)
                ?: throw UrlNotFoundException("URL not found for short code: $shortCode")

        return UrlDto(
            id = entity.id,
            originalUrl = entity.originalUrl,
            shortCode = entity.shortCode,
            totalClicks = entity.totalClicks,
            createdAt = entity.createdAt,
        )
    }

    @CachePut(value = ["urls"], key = "'url:' + #urlEntity.shortCode")
    fun putInCache(urlEntity: UrlEntity): UrlDto =
        UrlDto(
            id = urlEntity.id,
            originalUrl = urlEntity.originalUrl,
            shortCode = urlEntity.shortCode,
            totalClicks = urlEntity.totalClicks,
            createdAt = urlEntity.createdAt,
        )

    @CacheEvict(value = ["urls"], key = "'url:' + #shortCode")
    fun evictUrlCache(shortCode: String) {
        log.trace { "Evicted short code $shortCode from cache" }
    }
}
