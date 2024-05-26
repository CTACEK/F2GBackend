package com.ctacek.f2g.api.v1.requests.users.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpViaUsernameRequest(
    val username: String,
    val password: String,
    val avatar: Int = 1,
)