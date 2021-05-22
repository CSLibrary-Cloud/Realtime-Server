package com.example.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() { // 커넥할 루트 설정

    routing {
        get("/") {
            call.respondText("Hello World!2")
        }

        //여기에 커넥을 만들어야 함.

        get("/chat") {
            call.respondText("Hello World!3")
        }

        get("/RealTime"){
            call.respondText("Hello World!4")

        }

        get("/RealTime/leaderboard"){
            call.respondText("Hello World!4")
        }

        get("/RealTime/leftTime"){
            call.respondText("Hello World!4")
        }
    }
}
