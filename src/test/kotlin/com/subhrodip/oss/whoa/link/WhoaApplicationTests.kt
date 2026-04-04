package com.subhrodip.oss.whoa.link

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WhoaApplicationTests {
    @Test
    fun contextLoads() {
        assertDoesNotThrow {
            WhoaApplication()
        }
    }

    @Test
    fun `main method should run`() {
        assertDoesNotThrow {
            main(arrayOf(
                "--spring.main.web-application-type=none",
                "--spring.main.lazy-initialization=true",
                "--spring.liquibase.enabled=false",
                "--server.port=0",
                "--spring.main.banner-mode=off"
            ))
        }
    }
}
