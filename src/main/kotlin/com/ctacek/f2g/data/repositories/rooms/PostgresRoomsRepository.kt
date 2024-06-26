package com.ctacek.f2g.data.repositories.rooms

import com.ctacek.f2g.data.entities.RoomMembers
import com.ctacek.f2g.data.entities.Rooms
import com.ctacek.f2g.data.mappers.mapToRoom
import com.ctacek.f2g.domain.entities.RoomDTO
import com.ctacek.f2g.domain.entities.RoomDTO.Room
import com.ctacek.f2g.domain.entities.RoomDTO.RoomInfo
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.utils.UpdateModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*

class PostgresRoomsRepository(
    private val database: Database,
) : RoomsRepository {

    private val _updates = MutableSharedFlow<UpdateModel>()
    override val updates: Flow<UpdateModel>
        get() = _updates

    override suspend fun createRoom(room: Room): Boolean {
        var newRoomEntity = com.ctacek.f2g.data.entities.RoomEntity {
            id = room.id
            name = room.name
            date = room.date
            ownerId = room.ownerId
            gameStarted = false
        }
        var affectedRows = database.sequenceOf(Rooms).add(newRoomEntity)

        if (affectedRows != 1) { // if failed, try to change UUID
            newRoomEntity = newRoomEntity.copy()
            newRoomEntity.id = room.id + 1
            affectedRows = database.sequenceOf(Rooms).add(newRoomEntity)
        }

        return affectedRows == 1
    }

    override suspend fun deleteRoomById(id: String): Boolean {
        val affectedRows = database.sequenceOf(Rooms).find { it.id eq id }?.delete()
        return affectedRows == 1
    }

    override suspend fun getRoomById(id: String): Room? {
        val membersCount = database.sequenceOf(RoomMembers).filter { it.roomId eq id }
            .aggregateColumns { count(it.userId) }
        return database.sequenceOf(Rooms).find { it.id eq id }?.mapToRoom()?.copy(membersCount = membersCount ?: 1)
    }

    override suspend fun updateRoomById(id: String, newRoomData: RoomDTO.RoomUpdate): Boolean {
        val room = database.sequenceOf(Rooms).find { it.id eq id } ?: return false
        room.name = newRoomData.name ?: room.name
        room.date = newRoomData.date ?: room.date
        val affectedRows = room.flushChanges()
        return if (affectedRows == 1) {
            _updates.emit(UpdateModel.RoomUpdate(id, newRoomData))
            true
        } else {
            false
        }
    }

    override suspend fun getUserRooms(userId: String): List<RoomInfo> =
        database.from(Rooms)
            .innerJoin(RoomMembers, on = Rooms.id eq RoomMembers.roomId).select(
                Rooms.name,
                Rooms.id,
                Rooms.date,
                Rooms.ownerId,
                Rooms.ownerId,
                Rooms.gameStarted,
                RoomMembers.accepted,
            ).where {
                RoomMembers.userId eq userId
            }.map { room ->
                val membersCount = database.sequenceOf(RoomMembers).filter { it.roomId eq (room[Rooms.id] ?: "") }
                    .aggregateColumns { count(it.userId) }
                RoomInfo(
                    id = room[Rooms.id] ?: "",
                    shortName = room[Rooms.shortName] ?: "",
                    name = room[Rooms.name] ?: "",
                    date = room[Rooms.date],
                    ownerId = room[Rooms.ownerId] ?: "",
                    gameStarted = room[Rooms.gameStarted] ?: false,
                    membersCount = membersCount ?: 0,
                    accepted = room[RoomMembers.accepted]!!,
                )
            }
}