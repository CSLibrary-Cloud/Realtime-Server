package com.example.plugins

//import sun.management.jmxremote.ConnectorBootstrap.DefaultValues.PORT
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.*
import java.util.*
import kotlin.concurrent.timer


private val wsConnections = Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())


fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }


    // mongoDB 가져오기
 //   val uri = MongoClientURI(
  //      "mongodb+srv://kkalkkkal:monRkd1717!@cluster0.yrzya.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"
   // )

    //val mongoClient = MongoClient(uri)
    //val database: MongoDatabase = mongoClient.getDatabase("sample_Library")

    //val documentMongoCollection = MongoDatabase.getCollection("simple_Library")

    routing {

        webSocket("/") { // websocketSession
            for (frame in incoming) { // 수신 채널
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                }
            }
        }

        static("/static") {
            resources("static")
        }


        webSocket("/chat") {
            println("[snowdeer] chat starts")



            send("${LocalDateTime.now()}")

            while (true) {
                //val onlyDate: LocalDate = LocalDate.now()
                delay(1000)
                send("${LocalDateTime.now()}")
                println("${LocalDateTime.now()}")
                when (val frame = incoming.receive()) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        println("[client] text: $text")
                        outgoing.send(Frame.Text("$text from Server"))
                    }
                }
            }

            println("[snowdeer] chat is finished")
        }


        webSocket("/RealTime") {
            println("[client] chat starts")

            wsConnections += this
            try {
                while (true) {
                    delay(1000)
                    send("${LocalDateTime.now()}")
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            val text = frame.readText() // string 반환이니 보내고 싶은게 있으면 여기에다가 대체 해서 적을 것.

                            for (conn in wsConnections) {
                                conn.outgoing.send(Frame.Text(text))
                            }
                        }
                    }
                }
            } finally {
                wsConnections -= this
            }

            println("[client] chat is finished")
        }
    }

    startCoroutine()

}



private fun startCoroutine() {
    println("[snowdeer] startCoroutine()")

    GlobalScope.launch {
        var count = 0L
        while (true) {
            println("Coroutine is running...")

            count++
            for (conn in wsConnections) { // 연결된 모든 ws에 보냄.
                conn.outgoing.send(Frame.Text("hello($count)"))
            }

            delay(1000)
        }
    }
}


