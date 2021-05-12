package com.example.stomp_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StompServerApplication

fun main(args: Array<String>) {
    runApplication<StompServerApplication>(*args)
}
