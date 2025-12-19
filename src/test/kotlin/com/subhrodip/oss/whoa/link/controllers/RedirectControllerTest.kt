package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.services.UrlReadService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RedirectController::class)
class RedirectControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var urlReadService: UrlReadService

    @Test
    fun `should redirect to original url when short code exists`() {
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val userAgent = "Test Agent"
        val ipAddress = "127.0.0.1"

        whenever(urlReadService.getOriginalUrl(shortCode, userAgent, ipAddress)).thenReturn(originalUrl)

        mockMvc
            .perform(
                get("/$shortCode")
                    .header("User-Agent", userAgent)
                    .remoteAddress(ipAddress),
            ).andExpect(status().isFound)
            .andExpect(header().string("Location", originalUrl))
    }

    @Test
    fun `should return 404 when short code does not exist`() {
        val shortCode = "nonexistent"
        val userAgent = "Test Agent"
        val ipAddress = "127.0.0.1"

        whenever(urlReadService.getOriginalUrl(shortCode, userAgent, ipAddress)).thenThrow(UrlNotFoundException("Url not found"))

        mockMvc
            .perform(
                get("/$shortCode")
                    .header("User-Agent", userAgent)
                    .remoteAddress(ipAddress),
            ).andExpect(status().isNotFound)
    }
}
