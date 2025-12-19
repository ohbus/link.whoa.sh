package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.services.AnalyticsService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@WebMvcTest(UrlAnalyticsController::class)
class UrlAnalyticsControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var analyticsService: AnalyticsService

    private val analyticsPath = UrlConstants.ANALYTICS_PATH

    @Test
    fun `should return url analytics when short code exists`() {
        val shortCode = "abcdef"
        val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val analyticsResponse =
            UrlAnalyticsResponse(
                originalUrl = "https://example.com",
                shortUrl = shortCode,
                clicks = 10,
            )

        whenever(analyticsService.getUrlAnalytics(shortCode)).thenReturn(analyticsResponse)

        mockMvc
            .perform(get("${UrlConstants.API_V1_URLS}/$shortCode$analyticsPath"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(analyticsResponse)))
    }

    @Test
    fun `should return 404 when getting analytics for non-existent short code`() {
        val shortCode = "nonexistent"

        whenever(analyticsService.getUrlAnalytics(shortCode)).thenThrow(UrlNotFoundException("Url not found"))

        mockMvc
            .perform(get("${UrlConstants.API_V1_URLS}/$shortCode$analyticsPath"))
            .andExpect(status().isNotFound)
    }
}
