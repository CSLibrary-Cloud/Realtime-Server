package com.example.data

import com.example.plugins.WebSocketServer
import com.example.plugins.WebSocketServer.baseUrl
import com.example.plugins.client
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserConfigurer(
    private val session: DefaultWebSocketSession // Constructor Injection
) {
    // Current Session User
    private lateinit var currentUser: User

    // Mutex
    private val mutexLock: Mutex = Mutex()

    // User Token [By Lazy]
    private val userToken: String by lazy {
        runBlocking {
            WebSocketServer.getUserToken(session)
        }
    }

    // Coroutine Job
    private lateinit var timerJob: Job

    // Handle client message while executing timer
    suspend fun waitForClientMessage(): Boolean {
        while (session.isActive) {
            when (val frame: Frame = session.incoming.receive()) {
                is Frame.Text -> {
                    val incomingText: String = frame.readText()
                    handleClientMessage(incomingText)
                }
            }
        }

        return session.isActive
    }

    // Initialize current user and start timer.
    suspend fun setup() {
        // Setup User
        currentUser = WebSocketServer.requestToMainServer("${baseUrl}/api/v1/realtime/user",
            FindUserRequest(userToken)
        )

        // Setup Timer
        setupTimer()
    }

    // Called when client disconnects disrespectfully - Save and kick
    suspend fun handleForceDisconnection() {
        WebSocketServer.requestToMainServer<KickUserByToken, Void>("${baseUrl}/api/v1/realtime/kick",
            KickUserByToken(
                userToken = userToken,
                isTimeout = false,
                user = currentUser
            )
        )
    }

    // Setup countdown Timer
    private suspend fun setupTimer() {
        timerJob = GlobalScope.launch {
            // Main Countdown Timer
            while (currentUser.leftTime >= 0 && isActive) {
                countDown(session.outgoing)
            }

            // Finishing while loop gracefully means timeout has been occurred.
            handleTimeout(session.outgoing)
        }
    }

    // Count down timer and notify to client
    private suspend fun countDown(sender: SendChannel<Frame>) {
        delay(1000)
        mutexLock.withLock {
            currentUser.leftTime--
        }
        sender.send(Frame.Text("Hello!"))
    }

    // When Timeout occurs, this will be called and it will automatically kick out the user.
    private suspend fun handleTimeout(sender: SendChannel<Frame>) {
        // Timeout
        sender.send(Frame.Text("Your time has been expired!"))

        // Kick[Timeout]
        WebSocketServer.requestToMainServer<KickUserByToken, Void>(
            "${baseUrl}/api/v1/realtime/kick",
            KickUserByToken(
                userToken = userToken,
                isTimeout = true
            )
        )
    }

    // Handle Client Message
    private suspend fun handleClientMessage(incomingText: String) {
        when(incomingText) {
            "close" -> handleCloseConnection()
            "extend" -> handleExtendConnection()
        }
    }

    // Handle Session Close
    private suspend fun handleCloseConnection() {
        timerJob.cancelAndJoin()
        session.close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
    }

    // Handle Extension
    private suspend fun handleExtendConnection() {
        // TODO: LOCK AND SYNCHRONIZATION NEEDED
        mutexLock.withLock {
            if (currentUser.leftTime >= 60 * 60) {
                currentUser.leftTime += 60 * 60 * 2
            } else {
                session.outgoing.send(Frame.Text("Cannot extend time!"))
            }
        }
    }
}