package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.client.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApplicationTest {
    private lateinit var server: NettyApplicationEngine
    private val httpClient: HttpClient = HttpClient()

    @BeforeEach
    fun init() {
        server = embeddedServer(Netty, port = 8081, host = "localhost") { // 서버 생성
            configureSockets()
        }.start(wait = true)
    }

    @AfterEach
    fun destroy() {
        server.stop(500, 500)
    }
}