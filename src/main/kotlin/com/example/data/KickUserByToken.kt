package com.example.data

data class KickUserByToken(
    var userToken: String,
    var isTimeout: Boolean,
    var user: User? = null
)