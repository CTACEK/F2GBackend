package com.ctacek.f2g.api.v1.requests.rooms

import com.ctacek.f2g.utils.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreateRoomRequest(
    @SerialName("room_name")
    val username: String,
    val password: String?,
    val date:
    @Serializable(with = LocalDateSerializer::class)
    LocalDate?,
)