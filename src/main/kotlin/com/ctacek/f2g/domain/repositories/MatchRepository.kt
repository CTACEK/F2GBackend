package com.ctacek.f2g.domain.repositories

import com.ctacek.f2g.domain.entities.UserDTO
import com.ctacek.f2g.utils.UpdateModel
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    val updates: Flow<UpdateModel>
    suspend fun addToRoom(roomId: String, userId: String): Boolean
    suspend fun deleteFromRoom(roomId: String, userId: String): Boolean
    suspend fun getUsersInRoom(roomId: String): List<UserDTO.UserRoomInfo>
    suspend fun setGameState(roomId: String, state: Boolean): Boolean
    suspend fun checkUserInRoom(roomId: String, userId: String): Boolean
}