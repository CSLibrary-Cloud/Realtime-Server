package com.example.plugins

import io.ktor.application.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import java.time.Duration

object WebSocketServer {
    val baseUrl: String = "http://localhost:8080"
    // Init WebSocket
    fun initializeWebSocket(application: Application) {
        application.install(io.ktor.websocket.WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }

    // Function to get User Token
    suspend fun getUserToken(webSocketSession: DefaultWebSocketSession): String {
        return when(val inputFrame: Frame = webSocketSession.incoming.receive()) {
            is Frame.Text -> { inputFrame.readText() }
            else -> { "" }
        }
    }

    // Main Server Request[A Communicator with HTTP Server]
    suspend inline fun <REQ, reified RESPONSE> requestToMainServer(address: String, requestBody: REQ): RESPONSE {
        return client.get<RESPONSE>(address) {
            contentType(ContentType.Application.Json)
            body = requestBody!!
        }
    }
}