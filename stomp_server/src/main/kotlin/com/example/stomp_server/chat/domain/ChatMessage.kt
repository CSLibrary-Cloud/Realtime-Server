package com.example.stomp_server.chat.domain

// ChatMessage.kt

data class ChatMessage (
    var type: MessageType,
    var content: String?,
    var sender: String
)

