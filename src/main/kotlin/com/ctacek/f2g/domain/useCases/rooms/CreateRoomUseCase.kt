package com.ctacek.f2g.domain.useCases.rooms

import com.ctacek.f2g.domain.entities.RoomDTO
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.utils.getRandomRoomID
import com.ctacek.f2g.utils.getShortRoomName
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class CreateRoomUseCase : KoinComponent {
    private val roomsRepository: RoomsRepository by inject()
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        data class Successful(val room: RoomDTO.Room) : Result
        object UserNotExists : Result
        object Failed : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomName: String,
        date: LocalDate?,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val room = RoomDTO.Room(
            id = getRandomRoomID(),
            shortName = getShortRoomName(),
            name = roomName,
            date = date,
            ownerId = userId,
            gameStarted = false,
            membersCount = 1,
        )
        return if (roomsRepository.createRoom(room)) Result.Successful(room) else Result.Failed
    }
}