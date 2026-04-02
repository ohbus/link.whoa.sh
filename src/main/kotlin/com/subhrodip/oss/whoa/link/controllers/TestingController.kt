package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import com.subhrodip.oss.whoa.link.services.GlobalCounterService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/testing")
@Profile("dev")
@Tag(name = "Testing", description = "Internal hooks for E2E testing and database resetting")
class TestingController(
    private val urlRepository: UrlRepository,
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
    private val globalCounterService: GlobalCounterService,
    private val cacheManager: CacheManager,
) {
    @PostMapping("/reset")
    @Operation(summary = "Reset the application state", description = "Wipes all URLs, Analytics, Caches and resets the Global Counter.")
    fun resetState(): ResponseEntity<Map<String, String>> {
        log.warn { "E2E RESET TRIGGERED: Wiping all application state..." }

        // 1. Wipe Database (Order matters due to FKs)
        urlAnalyticsRepository.deleteAllInBatch()
        urlRepository.deleteAllInBatch()

        // 2. Clear All Caches
        cacheManager.cacheNames.forEach { cacheName ->
            cacheManager.getCache(cacheName)?.clear()
        }

        // 3. Reset In-Memory Global Counter
        globalCounterService.refreshFromDatabase()

        return ResponseEntity.ok(mapOf("status" to "success", "message" to "System state reset completed"))
    }
}
