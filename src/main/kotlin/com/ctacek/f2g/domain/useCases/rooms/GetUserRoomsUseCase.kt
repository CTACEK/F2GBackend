package com.ctacek.f2g.domain.useCases.rooms

import com.ctacek.f2g.domain.entities.RoomDTO
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetUserRoomsUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()

    sealed interface Result {
        data class Successful(val rooms: List<RoomDTO.RoomInfo>) : Result
        object UserNotExists : Result
    }

    suspend operator fun invoke(
        userId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val rooms = roomsRepository.getUserRooms(userId)
        return Result.Successful(rooms)
    }
}