package com.example.demo

import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSockHandler: TextWebSocketHandler() {

    private val sessionMap: HashMap<String, Job> = hashMapOf()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload: String = message.payload
        println("Payload input: $payload")

        val textMessage: TextMessage = TextMessage("Test Server!")
        session.sendMessage(textMessage)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)

        println("Putting ${session.id} in to a loop..")
        sessionMap[session.id] = GlobalScope.launch {
            while (isActive) {
                if (session.isOpen) {
                    session.sendMessage(TextMessage("Server Heartbeat"))
                    delay(500)
                }
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        super.afterConnectionClosed(session, status)
        println("Canceling CO-Routines")
        runBlocking {
            sessionMap[session.id]?.cancelAndJoin()
            sessionMap.remove(session.id)
            println("Finished!")
        }
    }
}