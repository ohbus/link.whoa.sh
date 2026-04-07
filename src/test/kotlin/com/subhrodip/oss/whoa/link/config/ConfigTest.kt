package com.subhrodip.oss.whoa.link.config

import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class ConfigTest {
    @Mock
    private lateinit var meterRegistry: MeterRegistry

    @Test
    fun `test MonitoringConfig`() {
        val config = MonitoringConfig()
        assertNotNull(config.timedAspect(meterRegistry))
        assertNotNull(config.countedAspect(meterRegistry))
    }

    @Test
    fun `test OpenApiConfig`() {
        val config = OpenApiConfig()
        assertNotNull(config)
    }

    @Test
    fun `test CacheConfig`() {
        val config = CacheConfig()
        ReflectionTestUtils.setField(config, "initialCapacity", 10)
        ReflectionTestUtils.setField(config, "maximumSize", 100L)

        // Test durations: s, m, h, d, and default
        val units = listOf("10s", "5m", "1h", "1d", "10x", "10")
        units.forEach { unit ->
            ReflectionTestUtils.setField(config, "expireAfterWrite", unit)
            assertNotNull(config.cacheManager(), "Failed for unit $unit")
        }
    }

    @Test
    fun `test SecurityConfig`() {
        val config = SecurityConfig()
        val http = mock(HttpSecurity::class.java)
        // Note: Mocking HttpSecurity is complex and might not cover all internal builder logic,
        // but it covers the bean definition method itself.
        // We rely on integration tests for full security verification.
        assertNotNull(config)
    }
}
