package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.exceptions.ShortCodeAlreadyExistsException
import com.subhrodip.oss.whoa.link.services.UrlWriteService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@WebMvcTest(UrlWriteController::class)
class UrlWriteControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var urlWriteService: UrlWriteService

    @Test
    fun `should create short url for valid request`() {
        val request =
            CreateShortUrlRequest(
                shortCode = "custom",
                url = "https://example.com",
            )
        val response =
            CreateShortUrlResponse(
                shortUrl = "http://localhost/custom",
                originalUrl = "https://example.com",
            )

        whenever(urlWriteService.createShortUrl(request)).thenReturn(response)

        mockMvc
            .perform(
                post(UrlConstants.API_V1_URLS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `should create short url for valid request without custom short code`() {
        val request =
            CreateShortUrlRequest(
                url = "https://another-example.com",
                shortCode = "generated",
            )
        val response =
            CreateShortUrlResponse(
                shortUrl = "http://localhost/generated",
                originalUrl = "https://another-example.com",
            )

        whenever(urlWriteService.createShortUrl(request)).thenReturn(response)

        mockMvc
            .perform(
                post(UrlConstants.API_V1_URLS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `should return 409 conflict when short code already exists`() {
        val request =
            CreateShortUrlRequest(
                url = "https://example.com",
                shortCode = "exists",
            )

        whenever(urlWriteService.createShortUrl(request)).thenThrow(ShortCodeAlreadyExistsException("Short code exists"))

        mockMvc
            .perform(
                post(UrlConstants.API_V1_URLS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isConflict)
    }

    @Test
    fun `should return 400 bad request for invalid url in request`() {
        val request =
            CreateShortUrlRequest(
                url = "not-a-valid-url",
                shortCode = "generated",
            )

        mockMvc
            .perform(
                post(UrlConstants.API_V1_URLS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 bad request for blank url in request`() {
        val request =
            CreateShortUrlRequest(
                url = "",
                shortCode = "generated",
            )

        mockMvc
            .perform(
                post(UrlConstants.API_V1_URLS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isBadRequest)
    }
}
