package com.ctacek.f2g.utils

import com.ctacek.f2g.domain.entities.RoomDTO
import com.ctacek.f2g.domain.entities.UserDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class UpdateModel {
    @SerialName("room_id")
    abstract val roomId: String

    @Serializable
    @SerialName("USERS_UPDATE")
    data class UsersUpdate(
        @SerialName("room_id") override val roomId: String,
        @SerialName("users_update") val usersUpdate: List<UserDTO.UserRoomInfo>,
    ) : UpdateModel()

    @Serializable
    @SerialName("GAME_STATE_UPDATE")
    data class GameStateUpdate(
        @SerialName("room_id") override val roomId: String,
        @SerialName("state_update") val stateUpdate: Boolean,
    ) : UpdateModel()

    @Serializable
    @SerialName("ROOM_UPDATE")
    data class RoomUpdate(
        @SerialName("room_id") override val roomId: String,
        @SerialName("room_update") val roomUpdate: RoomDTO.RoomUpdate,
    ) : UpdateModel()
}