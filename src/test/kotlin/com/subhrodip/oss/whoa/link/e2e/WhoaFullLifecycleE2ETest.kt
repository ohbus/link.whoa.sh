package com.subhrodip.oss.whoa.link.e2e

import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
@Testcontainers
class WhoaFullLifecycleE2ETest {
    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = RestTemplate()

    @Autowired
    private lateinit var urlRepository: UrlRepository

    companion object {
        @Container
        private val postgres =
            PostgreSQLContainer("postgres:16-alpine")
                .withDatabaseName("whoa_e2e")
                .withUsername("testuser")
                .withPassword("testpass")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.liquibase.contexts", { "e2e" })
        }
    }

    private fun baseUrl() = "http://localhost:$port"

    @Test
    fun `test full lifecycle create redirect and analytics`() {
        val originalUrl = "https://www.google.com"
        val customShortCode = "goog" + System.currentTimeMillis().toString().takeLast(6)

        // 1. Create Short URL
        val createRequest = CreateShortUrlRequest(url = originalUrl, shortCode = customShortCode)
        val createResponse = restTemplate.postForEntity("${baseUrl()}/api/v1/urls", createRequest, CreateShortUrlResponse::class.java)

        assertEquals(HttpStatus.CREATED, createResponse.statusCode)

        // 2. Redirect multiple times
        val headers = HttpHeaders()
        headers.set("User-Agent", "E2E-Tester")
        val entity = HttpEntity<Unit>(headers)

        repeat(3) {
            restTemplate.execute("${baseUrl()}/$customShortCode", HttpMethod.GET, { req -> req.headers.addAll(headers) }, { res ->
                assertEquals(HttpStatus.FOUND, res.statusCode)
            })
        }

        // 3. Verify Analytics (Polling to handle async processing)
        var lastClicks = -1L
        var success = false
        repeat(10) {
            val analyticsResponse =
                restTemplate.getForEntity(
                    "${baseUrl()}/api/v1/urls/$customShortCode/analytics",
                    UrlAnalyticsResponse::class.java,
                )
            lastClicks = analyticsResponse.body?.clicks ?: -1L
            if (lastClicks == 3L) {
                success = true
                return@repeat
            }
            Thread.sleep(500)
        }
        assertTrue(success, "Expected 3 clicks, but got $lastClicks after 5 seconds")
    }

    @Test
    fun `test cache resilience after database deletion`() {
        val originalUrl = "https://example.com"
        val shortCode = "resil" + System.currentTimeMillis().toString().takeLast(5)

        val createRequest = CreateShortUrlRequest(url = originalUrl, shortCode = shortCode)
        restTemplate.postForEntity("${baseUrl()}/api/v1/urls", createRequest, CreateShortUrlResponse::class.java)

        val dbEntity = urlRepository.findByShortCode(shortCode)
        assertNotNull(dbEntity)
        urlRepository.delete(dbEntity!!)

        try {
            restTemplate.getForEntity("${baseUrl()}/$shortCode", Void::class.java)
        } catch (e: HttpClientErrorException) {
            assertEquals(HttpStatus.NOT_FOUND, e.statusCode)
        }
    }

    @Test
    fun `test concurrent creation of same short code`() {
        val shortCode = "race" + System.currentTimeMillis().toString().takeLast(6)
        val url = "https://race.com"
        val request = CreateShortUrlRequest(url = url, shortCode = shortCode)

        val executor = Executors.newFixedThreadPool(10)
        val tasks =
            (1..10).map {
                Callable<HttpStatus> {
                    try {
                        restTemplate.postForEntity("${baseUrl()}/api/v1/urls", request, Void::class.java).statusCode as HttpStatus
                    } catch (e: HttpClientErrorException) {
                        HttpStatus.valueOf(e.statusCode.value())
                    }
                }
            }

        val results = executor.invokeAll(tasks).map { it.get() }
        executor.shutdown()

        assertEquals(1, results.count { it == HttpStatus.CREATED }, "Exactly one should succeed")
        assertTrue(results.count { it == HttpStatus.CONFLICT } >= 9, "Others should conflict")
    }

    @Test
    fun `test health endpoint`() {
        val healthResponse = restTemplate.getForEntity("${baseUrl()}/actuator/health", Map::class.java)
        assertEquals(HttpStatus.OK, healthResponse.statusCode)
        assertEquals("UP", healthResponse.body?.get("status"))
    }
}
