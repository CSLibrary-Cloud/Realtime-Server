package com.example.Realtime_Server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Duration
import io.ktor.application.Application
import io.ktor.features.AutoHeadResponse.install
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.springframework.web.servlet.function.RouterFunctions.resources


@SpringBootApplication
class RealtimeServerApplication


fun Application.main() {

	install(WebSockets) {
		pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
		timeout = Duration.ofSeconds(15)
		maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length.
		masking = false
	}

	routing {
		static("/static") {
			resources("static")
		}

		webSocket("/chat") {
			println("[snowdeer] chat starts")
			while (true) {
				when (val frame = incoming.receive()) {
					is Frame.Text -> {
						val text = frame.readText()
						println("[snowdeer] text: $text")
						outgoing.send(Frame.Text("$text from Server"))
					}
				}
			}

			println("[snowdeer] chat is finished")
		}
	}
}
