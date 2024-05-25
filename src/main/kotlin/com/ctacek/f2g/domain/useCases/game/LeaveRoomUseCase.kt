package com.ctacek.f2g.domain.useCases.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository

class LeaveRoomUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val matchRepository: MatchRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object UserNotInRoom : Result
        object RoomNotFound : Result
        object UserNotFound : Result
        object GameAlreadyStarted : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotFound
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotFound
        if (matchRepository.getUsersInRoom(roomId).find { it.userId == userId } == null) return Result.UserNotInRoom
        if (room.gameStarted) return Result.GameAlreadyStarted

        var res = matchRepository.deleteFromRoom(
            roomId = roomId,
            userId = userId,
        )

        if (room.ownerId == userId) {
            res = res && roomsRepository.deleteRoomById(roomId)
        }

        return if (res) Result.Successful else Result.Failed
    }
}