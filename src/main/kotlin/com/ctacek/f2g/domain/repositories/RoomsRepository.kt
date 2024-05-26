package com.ctacek.f2g.domain.repositories

import com.ctacek.f2g.domain.entities.RoomDTO
import com.ctacek.f2g.domain.entities.RoomDTO.Room
import com.ctacek.f2g.domain.entities.RoomDTO.RoomInfo
import com.ctacek.f2g.utils.UpdateModel
import kotlinx.coroutines.flow.Flow

interface RoomsRepository {

    val updates: Flow<UpdateModel>

    suspend fun createRoom(room: Room): Boolean
    suspend fun deleteRoomById(id: String): Boolean
    suspend fun getRoomById(id: String): Room?
    suspend fun updateRoomById(id: String, newRoomData: RoomDTO.RoomUpdate): Boolean
    suspend fun getUserRooms(userId: String): List<RoomInfo>
}