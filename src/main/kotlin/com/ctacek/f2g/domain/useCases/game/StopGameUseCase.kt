package com.ctacek.f2g.domain.useCases.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository

class StopGameUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val matchRepository: MatchRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object RoomNotFound : Result
        object Forbidden : Result
        object UserNotFound : Result
        object GameAlreadyStopped : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotFound
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotFound
        if (room.ownerId != userId) return Result.Forbidden
        if (!room.gameStarted) return Result.GameAlreadyStopped
        val res = matchRepository.setGameState(roomId, false) && matchRepository.deleteRecipients(roomId)
        return if (res) Result.Successful else Result.Failed
    }
}