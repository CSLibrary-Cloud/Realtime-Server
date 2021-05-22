package com.example.data

import org.bson.types.ObjectId

enum class UserState {
    BREAK, EXIT, START
}


data class User(
    var id: ObjectId = ObjectId(),
    var userId: String = "",
    var userPassword: String = "",
    var userName: String = "",
    var userPhoneNumber: String = "",
    var roles: Set<String> = setOf(),
    var startTime: Long = -1,
    var leftTime: Long = -1,
    var endTime: Long = -1,
    var reservedSeatNumber: String = "",
    var userState: UserState = UserState.EXIT
)