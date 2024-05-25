package com.ctacek.f2g.api.v1.requests.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KickUserRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("room_id") val roomId: String,
)