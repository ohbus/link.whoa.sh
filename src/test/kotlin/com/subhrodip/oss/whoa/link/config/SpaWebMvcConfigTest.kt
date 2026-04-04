package com.subhrodip.oss.whoa.link.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.CorsRegistration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceChainRegistration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.resource.PathResourceResolver

import org.mockito.ArgumentCaptor
import org.springframework.core.io.ClassPathResource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class SpaWebMvcConfigTest {
    @InjectMocks
    lateinit var spaWebMvcConfig: SpaWebMvcConfig

    @Test
    fun `addCorsMappings should configure cors correctly`() {
        val registry = mock(CorsRegistry::class.java)
        val registration = mock(CorsRegistration::class.java)

        `when`(registry.addMapping(anyString())).thenReturn(registration)
        `when`(registration.allowedOriginPatterns(anyString())).thenReturn(registration)
        `when`(
            registration.allowedMethods(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()),
        ).thenReturn(registration)
        `when`(registration.allowedHeaders(anyString())).thenReturn(registration)
        `when`(registration.allowCredentials(anyBoolean())).thenReturn(registration)
        `when`(registration.maxAge(anyLong())).thenReturn(registration)

        spaWebMvcConfig.addCorsMappings(registry)

        verify(registry).addMapping("/api/**")
        verify(registration).allowedOriginPatterns("*")
        verify(registration).maxAge(3600L)
    }

    @Test
    fun `addResourceHandlers should configure handlers and resolver correctly`() {
        val registry = mock(ResourceHandlerRegistry::class.java)
        val registration = mock(ResourceHandlerRegistration::class.java)
        val chain = mock(ResourceChainRegistration::class.java)
        val resolverCaptor = ArgumentCaptor.forClass(PathResourceResolver::class.java)

        `when`(registry.addResourceHandler(anyString())).thenReturn(registration)
        `when`(registration.addResourceLocations(anyString())).thenReturn(registration)
        `when`(registration.resourceChain(anyBoolean())).thenReturn(chain)
        `when`(chain.addResolver(resolverCaptor.capture())).thenReturn(chain)

        spaWebMvcConfig.addResourceHandlers(registry)

        val resolver = resolverCaptor.value
        val getResourceMethod = PathResourceResolver::class.java.getDeclaredMethod(
            "getResource", 
            String::class.java, 
            Resource::class.java
        )
        getResourceMethod.isAccessible = true
        
        // Test resolver with existing resource
        val location = mock(Resource::class.java)
        val resource = mock(Resource::class.java)
        `when`(location.createRelative("test.js")).thenReturn(resource)
        `when`(resource.exists()).thenReturn(true)
        `when`(resource.isReadable).thenReturn(true)
        
        val result = getResourceMethod.invoke(resolver, "test.js", location) as Resource
        assertEquals(resource, result)
        
        // Test resolver with non-existing resource (should return index.html)
        val nonExistent = mock(Resource::class.java)
        `when`(location.createRelative("non-existent")).thenReturn(nonExistent)
        `when`(nonExistent.exists()).thenReturn(false)
        
        val resultIndex = getResourceMethod.invoke(resolver, "non-existent", location) as Resource
        assertTrue(resultIndex is ClassPathResource)
        assertEquals("static/index.html", (resultIndex as ClassPathResource).path)
    }
}
