package com.ctacek.f2g.api.v1.responses

import com.ctacek.f2g.domain.entities.UserDTO
import com.ctacek.f2g.utils.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class InfoDetails(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("room_name")
    val roomName: String,
    @SerialName("owner_id")
    val ownerId: String,
    val date:
        @Serializable(with = LocalDateSerializer::class)
        LocalDate?,
    val users: List<UserDTO.UserRoomInfo>,
)