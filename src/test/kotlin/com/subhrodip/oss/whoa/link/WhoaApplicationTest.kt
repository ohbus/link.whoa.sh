package com.subhrodip.oss.whoa.link

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class WhoaApplicationTest {

    @Test
    fun contextLoads() {
        // This test ensures the application context loads successfully.
    }

    @Test
    fun `test main method`() {
        // We call main with a random port to avoid conflicts and just cover the line
        main(arrayOf("--server.port=0", "--spring.main.banner-mode=off"))
    }
}
