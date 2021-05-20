package com.example

import io.ktor.client.*
import io.ktor.client.features.websocket.*

import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.netty.util.Timer
import io.netty.util.TimerTask
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask


suspend fun main() {
    val client = HttpClient {
        install(WebSockets)
    }


    client.ws(
        method = HttpMethod.Get,
        host = "localhost",
        port = 8080, path = "/chat"
    ) { // this: DefaultClientWebSocketSession

        // Send text frame.
        send("Hello, Text frame")

        // Send text frame.
        send(Frame.Text("Hello World"))

        // Send binary frame.
  //      send(Frame.Binary(0101))

        // Receive frame.
        while(true) {
            send(Frame.Text("Hello World"))
            when (val frame = incoming.receive()) {

                is Frame.Text -> println(frame.readText())
          //      is Frame.Binary -> println(frame.readBytes())
            }
        }
    }


    client.close()
}
