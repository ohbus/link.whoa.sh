package com.subhrodip.oss.whoa.link

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class WhoaApplication

fun main(args: Array<String>) {
    runApplication<WhoaApplication>(*args)
}
