package com.ctacek.f2g.domain.useCases.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository

class AcceptUserUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val matchRepository: MatchRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object Forbidden : Result
        object UserNotInRoom : Result
        object RoomNotFound : Result
        object UserNotFound : Result
    }

    suspend operator fun invoke(
        selfId: String, // ID of user that requested acceptation
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotFound
        if (matchRepository.getUsersInRoom(roomId).find { it.userId == userId } == null) return Result.UserNotInRoom
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotFound
        if (room.ownerId != selfId) return Result.Forbidden
        return if (matchRepository.acceptUser(
                roomId = roomId,
                userId = userId,
            )
        ) {
            Result.Successful
        } else {
            Result.Failed
        }
    }
}