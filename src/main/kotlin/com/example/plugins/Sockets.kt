package com.example.plugins

import com.example.data.UserConfigurer
import io.ktor.application.Application
import io.ktor.client.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Application.configureSockets(client: HttpClient) {
    // Init Socket
    val webSocketServer: WebSocketServer = WebSocketServer(client).apply {
        initializeWebSocket(this@configureSockets)
    }

    routing {
        webSocket("/realtime/endpoint") {
            val userConfigurer: UserConfigurer = UserConfigurer(this, webSocketServer)
            runCatching {
                // This will start timer
                userConfigurer.setup()

                // While coroutine[timer] run, receive string from client if any.
                userConfigurer.waitForClientMessage()
            }.onFailure {
                // Force Close Handle
                println("Client exited abnormally!!")
                println("Debug Trace: ${it.stackTraceToString()}")
                userConfigurer.handleForceDisconnection()
            }
        }
    }
}