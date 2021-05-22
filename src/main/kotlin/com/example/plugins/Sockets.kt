package com.example.plugins

import com.example.UserConfigurer
import io.ktor.application.Application
import io.ktor.client.*
import io.ktor.routing.*
import io.ktor.websocket.*

val client: HttpClient = HttpClient()

fun Application.configureSockets() {
    // Init Socket
    WebSocketServer.initializeWebSocket(this)

    routing {
        webSocket("/realtime/endpoint") {
            val userConfigurer: UserConfigurer = UserConfigurer(this)
            runCatching {
                // This will start timer
                userConfigurer.setup()

                // While coroutine[timer] run, receive string from client if any.
                userConfigurer.waitForClientMessage()
            }.onFailure {
                // Force Close Handle
                userConfigurer.handleForceDisconnection()
            }
        }
    }
}