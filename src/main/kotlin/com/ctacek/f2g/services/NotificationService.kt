package com.ctacek.f2g.services

import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.entities.Notification
import com.ctacek.f2g.domain.entities.NotificationMessage
import com.ctacek.f2g.domain.repositories.GameRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.domain.services.OneSignalService
import com.ctacek.f2g.utils.UpdateModel

class NotificationService : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val gameRepository: GameRepository by inject()

    private val oneSignalService: OneSignalService by inject()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val roomUpdates = async {
                roomsRepository.updates.collect {
                    if (it !is UpdateModel.RoomUpdate) return@collect
                    notifyRoomUpdated(
                        roomId = it.roomId,
                        message = NotificationMessage(
                            en = "Room updated!",
                            ru = "Комната обновлена!",
                        ),
                    )
                }
            }

            val gameUpdates = async {
                gameRepository.updates.collect {
                    val message = when (it) {
                        is UpdateModel.GameStateUpdate -> NotificationMessage(
                            en = "Game state updated!",
                            ru = "У комнаты новый статус!",
                        )

                        is UpdateModel.UsersUpdate -> NotificationMessage(
                            en = "New user in the room!",
                            ru = "В комнате изменился состав игроков!",
                        )

                        else -> return@collect
                    }
                    notifyRoomUpdated(
                        roomId = it.roomId,
                        message = message,
                        forOwner = it is UpdateModel.UsersUpdate, // send only for room owner if users set changed
                    )
                }
            }
            awaitAll(roomUpdates, gameUpdates)
        }
    }

    private suspend fun notifyRoomUpdated(
        roomId: String,
        message: NotificationMessage,
        forOwner: Boolean = false,
    ) {
        val room = roomsRepository.getRoomById(roomId) ?: return
        val users =
            if (forOwner) {
                usersRepository.getUserByID(room.ownerId)?.clientIds ?: emptyList()
            } else {
                gameRepository
                    .getUsersInRoom(roomId)
                    .map { user ->
                        usersRepository.getUserByID(user.userId)
                    }
                    .flatMap { user -> user?.clientIds ?: emptyList() }
                    .distinct() // distinct required if multiple users has same client id
            }
        println(users)
        oneSignalService.sendNotification(
            Notification(
                usersId = users,
                headings = NotificationMessage(
                    en = room.name,
                    ru = room.name,
                ),
                contents = message,
                appId = OneSignalService.APP_ID,
            ),
        )
    }
}