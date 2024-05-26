package com.ctacek.f2g.api.v1.requests.users.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginViaUsernameRequest(
    val username: String,
    val password: String,
)