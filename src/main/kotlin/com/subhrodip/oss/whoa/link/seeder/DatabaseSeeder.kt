package com.subhrodip.oss.whoa.link.seeder

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import kotlin.random.Random

private val log = KotlinLogging.logger {}

@Component
@Profile("dev")
class DatabaseSeeder(
    private val urlRepository: UrlRepository,
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String) {
        if (urlRepository.count() > 0) {
            log.info { "Database already seeded. Skipping..." }
            return
        }

        log.info { "Seeding rich data for local development..." }

        val baseUrls =
            listOf(
                "https://github.com/ohbus/link.whoa.sh",
                "https://spring.io/projects/spring-boot",
                "https://kotlinlang.org/docs/home.html",
                "https://angular.dev/overview",
                "https://www.postgresql.org/docs/",
                "https://docker.com",
                "https://news.ycombinator.com",
                "https://reddit.com",
            )

        val userAgents =
            listOf(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/123.0.6312.52 Mobile/15E148 Safari/604.1",
            )

        val ipAddresses = listOf("192.168.1.1", "10.0.0.45", "172.16.254.1", "8.8.8.8", "1.1.1.1")

        val now = OffsetDateTime.now()

        for ((index, url) in baseUrls.withIndex()) {
            val shortCode = "dev00${index + 1}"
            val urlEntity =
                urlRepository.save(
                    UrlEntity(
                        originalUrl = url,
                        shortCode = shortCode,
                    ),
                )

            // Seed random analytics for each URL spread over the last 30 days
            val clickCount = Random.nextInt(50, 300)
            val analyticsToSave = mutableListOf<UrlAnalyticsEntity>()
            for (i in 1..clickCount) {
                val randomDaysAgo = Random.nextLong(0, 30)
                val randomHoursAgo = Random.nextLong(0, 24)
                val createdAt = now.minusDays(randomDaysAgo).minusHours(randomHoursAgo)

                analyticsToSave.add(
                    UrlAnalyticsEntity(
                        urlEntity = urlEntity,
                        userAgent = userAgents.random(),
                        ipAddress = ipAddresses.random(),
                    ).apply {
                        this.createdAt = createdAt
                    },
                )
            }
            urlAnalyticsRepository.saveAll(analyticsToSave)
        }

        log.info { "Database seeding completed successfully." }
    }
}
