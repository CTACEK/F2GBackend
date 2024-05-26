package com.ctacek.f2g.domain.entities

import com.ctacek.f2g.utils.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

sealed interface RoomDTO {

    @Serializable
    data class Room(
        @SerialName("room_id") val id: String,
        @SerialName("room_name") val name: String,
        @SerialName("room_short_name") val shortName: String,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
        @SerialName("owner_id") val ownerId: String,
        @SerialName("game_started") val gameStarted: Boolean = false,
        @SerialName("members_count") val membersCount: Int,
    ) : RoomDTO

    @Serializable
    data class RoomUpdate(
        @SerialName("room_name") val name: String? = null,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate? = null,
    ) : RoomDTO

    @Serializable
    data class RoomInfo(
        @SerialName("room_id") val id: String,
        @SerialName("room_name") val name: String,
        @SerialName("room_short_name") val shortName: String,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
        @SerialName("owner_id") val ownerId: String,
        @SerialName("game_started") val gameStarted: Boolean = false,
        @SerialName("members_count") val membersCount: Int,
        val accepted: Boolean = false,
    ) : RoomDTO
}