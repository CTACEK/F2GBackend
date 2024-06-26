package com.ctacek.f2g.api.v1.requests.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val username: String,
    val avatar: Int?,
)