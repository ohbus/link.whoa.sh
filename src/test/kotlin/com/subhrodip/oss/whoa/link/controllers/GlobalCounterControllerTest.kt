package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.services.GlobalCounterService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GlobalCounterControllerTest {

    @Mock
    lateinit var globalCounterService: GlobalCounterService

    @InjectMocks
    lateinit var globalCounterController: GlobalCounterController

    @Test
    fun `getGlobalClicks should return total clicks from service`() {
        `when`(globalCounterService.getTotalClicks()).thenReturn(500L)

        val response = globalCounterController.getGlobalClicks()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(500L, response.body?.totalClicks)
        assert(response.body?.serverTimestamp!! > 0L)
    }
}
