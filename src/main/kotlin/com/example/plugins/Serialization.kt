package com.example.plugins

import io.ktor.jackson.*
import com.fasterxml.jackson.databind.*
import io.ktor.features.*
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*


data class PostItem(val id: String, val email: String) // POST로 받음.

fun Application.configureSerialization() {

    val list = ArrayList<PostItem>()

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }


    // JSON을 어떻게 보낼지 매핑 API(response)
    //http://localhost:8080/{$route}로 리퀘스트를 날리면 응답
    routing {
        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        static("/static") {
            resources("static")
        }

        // 이하 경로를 호출하면 적혀진 JSON이 응답됨.
        routing {
            get("/RealTime") {

                val map = HashMap<String, Any>()
                map["status"] == 0
                map["ClientID"] = "clientID"
                map["ClientPort"] = 40
                map["ClientName"] = "client_name"
                map["ClientIP"] = "127.0.0.1"
                map["reservedSeatNumber"] = 3
                map["userState"] = "Login"
                map["Login_Timeline"] = "00:00:00"
                map["Cumulative_Timeline"] = "24:00:00"

                call.respond(mapOf("data" to map))
            }

            get("/RealTime/leaderboard") {
                val map2 = HashMap<String, Any>()
                map2["status"] == 1
                map2["ClientID"] = "clientID"
                map2["ClientPort"] = 40
                map2["ClientName"] = "client_name1"
                map2["ClientIP"] = "127.0.0.2"
                map2["reservedSeatNumber"] = 3
                map2["userState"] = "Left"
                map2["Login_Timeline"] = "00:00:01"
                map2["Cumulative_Timeline"] = "24:00:01"

                call.respond(mapOf("leaderboard" to map2))
            }

            get("/RealTime/leftTime") {
                val map3 = HashMap<String, Any>()
                map3["status"] == 2
                map3["ClientID"] = "clientID"
                map3["ClientPort"] = 50
                map3["ClientName"] = "client_name2"
                map3["ClientIP"] = "127.0.0.3"
                map3["reservedSeatNumber"] = 4
                map3["userState"] = "LogOut"
                map3["Login_Timeline"] = "00:00:02"
                map3["Cumulative_Timeline"] = "24:00:02"

                call.respond(mapOf("leftTime" to map3))
            }


            // HTTP POST를 이용한 데이터 수신
            post("/RealTime/add") {
                val data = call.receive<PostItem>()
                println("[clientID] receive: ${data}")

                list.add(data)

                call.respond(mapOf("result" to true))
            }

            get("/RealTime/list") {
                call.respond(mapOf("data" to list))
            }

        }
    }


}
