package com.subhrodip.oss.whoa.link

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
@EnableCaching
class WhoaApplication

fun main(args: Array<String>) {
    runApplication<WhoaApplication>(*args)
}
