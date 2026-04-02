package com.subhrodip.oss.whoa.link.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info =
        Info(
            title = "Whoa URL Shortener API",
            version = "1.0.0",
            description = "A lightweight, high-performance URL shortener service with analytics and caching.",
            contact =
                Contact(
                    name = "Whoa Support",
                    email = "support@link.whoa.sh",
                ),
            license =
                License(
                    name = "Apache 2.0",
                    url = "https://www.apache.org/licenses/LICENSE-2.0",
                ),
        ),
)
class OpenApiConfig
