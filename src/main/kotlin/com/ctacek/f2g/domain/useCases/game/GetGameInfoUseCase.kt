package com.ctacek.f2g.domain.useCases.game

import com.ctacek.f2g.api.v1.responses.InfoDetails
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetGameInfoUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val matchRepository: MatchRepository by inject()

    sealed interface Result {
        data class Successful(val info: InfoDetails) : Result
        object UserNotExists : Result
        object RoomNotExists : Result
        object Forbidden : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotExists
        val users = matchRepository.getUsersInRoom(roomId)

        if (users.find { it.userId == userId } == null) return Result.Forbidden

        val info = InfoDetails(
            roomId = room.id,
            roomName = room.name,
            ownerId = room.ownerId,
            date = room.date,
            users = users,
        )
        return Result.Successful(info)
    }
}