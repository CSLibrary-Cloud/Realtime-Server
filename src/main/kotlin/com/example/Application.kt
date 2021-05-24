package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*

fun main() {
    val httpClient: HttpClient = HttpClient {
        install(WebSockets)
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }
    embeddedServer(Netty, port = 8081, host = "localhost") { // 서버 생성
        configureSockets(httpClient)
    }.start(wait = true)
}
