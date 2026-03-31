package com.subhrodip.oss.whoa.link.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig {
    @Value("\${app.cache.urls.initial-capacity:100}")
    private var initialCapacity: Int = 100

    @Value("\${app.cache.urls.maximum-size:500}")
    private var maximumSize: Long = 500

    @Value("\${app.cache.urls.expire-after-write:10m}")
    private lateinit var expireAfterWrite: String

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("urls")

        val duration = parseDuration(expireAfterWrite)

        cacheManager.setCaffeine(
            Caffeine
                .newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(duration.first, duration.second)
                .recordStats(),
        )
        return cacheManager
    }

    private fun parseDuration(duration: String): Pair<Long, TimeUnit> {
        val value = duration.dropLast(1).toLong()
        val unit =
            when (duration.last().lowercaseChar()) {
                's' -> TimeUnit.SECONDS
                'm' -> TimeUnit.MINUTES
                'h' -> TimeUnit.HOURS
                'd' -> TimeUnit.DAYS
                else -> TimeUnit.MINUTES
            }
        return Pair(value, unit)
    }
}
