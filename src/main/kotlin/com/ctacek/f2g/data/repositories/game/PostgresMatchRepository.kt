package com.ctacek.f2g.data.repositories.game

import com.ctacek.f2g.data.entities.*
import com.ctacek.f2g.domain.entities.UserDTO
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.utils.UpdateModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class PostgresMatchRepository(
    private val database: Database,
) : MatchRepository {

    private val _updates = MutableSharedFlow<UpdateModel>()
    override val updates: Flow<UpdateModel>
        get() = _updates

    override suspend fun addToRoom(roomId: String, userId: String): Boolean {
        val newMember = RoomMember {
            this.roomEntityId = database.sequenceOf(Rooms).find { it.id eq roomId } ?: return false
            this.userId = database.sequenceOf(Users).find { it.userId eq userId } ?: return false
            this.accepted = false
        }
        val affectedRows = database.sequenceOf(RoomMembers).add(newMember)

        return if (affectedRows == 1) {
            _updates.emit(UpdateModel.UsersUpdate(roomId = roomId, usersUpdate = getUsersInRoom(roomId)))
            true
        } else {
            false
        }
    }

    override suspend fun deleteFromRoom(roomId: String, userId: String): Boolean {
        val affectedRows = database.delete(RoomMembers) { (it.userId eq userId) and (it.roomId eq roomId) }
        return if (affectedRows == 1) {
            _updates.emit(UpdateModel.UsersUpdate(roomId = roomId, usersUpdate = getUsersInRoom(roomId = roomId)))
            true
        } else {
            false
        }
    }

    override suspend fun getUsersInRoom(roomId: String): List<UserDTO.UserRoomInfo> {
        return database.from(RoomMembers).innerJoin(Users, on = RoomMembers.userId eq Users.userId)
            .innerJoin(Avatars, on = Users.avatar eq Avatars.id)
            .select(
                Users.userId,
                Avatars.image,
                RoomMembers.accepted,
            ).where {
                RoomMembers.roomId eq roomId
            }.map { row ->
                UserDTO.UserRoomInfo(
                    userId = row[Users.userId]!!,
                    username = row[Users.username]!!,
                    avatar = row[Avatars.image]!!,
                )
            }
    }

    override suspend fun setGameState(roomId: String, state: Boolean): Boolean {
        val affectedRows = database.update(Rooms) {
            set(it.gameStarted, state)
            where {
                it.id eq roomId
            }
        }
        return if (affectedRows == 1) {
            _updates.emit(UpdateModel.GameStateUpdate(roomId, state))
            true
        } else {
            false
        }
    }

    override suspend fun checkUserInRoom(roomId: String, userId: String): Boolean {
        return database.sequenceOf(RoomMembers).find { (it.roomId eq roomId) and (it.userId eq userId) } != null
    }
}