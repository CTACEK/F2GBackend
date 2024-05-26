package com.ctacek.f2g.api.v1.requests.rooms

import com.ctacek.f2g.utils.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpdateRoomRequest(
    @SerialName("room_name") val name: String?,
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
)