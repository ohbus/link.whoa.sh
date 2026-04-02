package com.subhrodip.oss.whoa.link.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .headers { headers ->
                headers
                    .frameOptions { it.deny() }
                    .xssProtection { it.disable() } // Handled by browser or modern CSP
                    .contentSecurityPolicy { it.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self';") }
                    .referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN) }
                    .httpStrictTransportSecurity { it.includeSubDomains(true).maxAgeInSeconds(31536000) }
            }

        return http.build()
    }
}
