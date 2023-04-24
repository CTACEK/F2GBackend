package com.ctacek.f2g.domain.useCases.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.repositories.GameRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository

class JoinRoomUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val gameRepository: GameRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object RoomNotFound : Result
        object UserNotFound : Result
        object GameAlreadyStarted : Result
        object UserAlreadyInRoom : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
        wishlist: String?,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotFound
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotFound
        if (gameRepository.checkUserInRoom(roomId, userId)) return Result.UserAlreadyInRoom
        if (room.gameStarted) return Result.GameAlreadyStarted

        return if (gameRepository.addToRoom(room.id, userId, wishlist)) {
            Result.Successful
        } else {
            Result.Failed
        }
    }
}