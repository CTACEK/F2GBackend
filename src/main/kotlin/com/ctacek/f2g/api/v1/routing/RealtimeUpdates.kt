package com.ctacek.f2g.api.v1.routing

import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.utils.UpdateModel
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    encodeDefaults = true
    explicitNulls = false
}

fun Route.webSockets(
    usersRepository: UsersRepository,
    roomsRepository: RoomsRepository,
    matchRepository: MatchRepository,
) {
    authenticate {
        webSocket {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                send(Frame.Text("No access token provided"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No access token provided"))
                return@webSocket
            }
            val roomId = call.request.queryParameters["id"] ?: run {
                send(Frame.Text("Wrong room id"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Wrong room id"))
                return@webSocket
            }
            if (usersRepository.getUserByID(userId) == null) {
                send(Frame.Text("User not exists"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not exists"))
                return@webSocket
            }

            if (!matchRepository.checkUserInRoom(roomId = roomId, userId = userId)) {
                send(Frame.Text("User not in the room"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not in the room"))
                return@webSocket
            }

            val roomUpdateHandler = async {
                roomsRepository.updates
                    .collect { update ->
                        if (update !is UpdateModel.RoomUpdate) return@collect
                        if (update.roomId != roomId) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }

            val gameUpdateHandler = async {
                matchRepository.updates
                    .collect { update ->
                        if (update is UpdateModel.GameStateUpdate && update.roomId != roomId) return@collect
                        if (update is UpdateModel.UsersUpdate && update.roomId != roomId) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }
            awaitAll(roomUpdateHandler, gameUpdateHandler)
        }
    }

//    authenticate {
//        webSocket("/match") {
//            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
//                send(Frame.Text("No access token provided"))
//                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No access token provided"))
//                return@webSocket
//            }
//
//            val userMovies = MoviesRepository.getRandomMovies(5)
//            send(Frame.Text(Json.encodeToString(userMovies)))
//
//            for (frame in incoming) {
//                frame as? Frame.Text ?: continue
//                val userLikes = Json.decodeFromString<List<Boolean>>(frame.readText())
//                userLikes.forEachIndexed { index, like ->
//                    UserLikesRepository.addLike(UserLike(userId, userMovies[index].id, like))
//                }
//
//                // Проверка мэтчей
//                userMovies.forEach { movie ->
//                    val likes = UserLikesRepository.getLikesForMovie(movie.id)
//                    if (likes.count { it.like } > 1) {  // Простая логика мэтчинга, если более одного лайка
//                        val matchedUsers = likes.map { it.userId }.distinct()
//                        send(Frame.Text("Match found for movie ${movie.title} with users $matchedUsers"))
//                    }
//                }
//            }
//        }
//    }

    authenticate {
        webSocket("/all") { // subscribe on all user's rooms updates
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                send(Frame.Text("No access token provided"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No access token provided"))
                return@webSocket
            }
            if (usersRepository.getUserByID(userId) == null) {
                send(Frame.Text("User not exists"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not exists"))
                return@webSocket
            }
            val userRooms = roomsRepository.getUserRooms(userId)

            val roomUpdateHandler = async {
                roomsRepository.updates
                    .collect { update ->
                        if (update !is UpdateModel.RoomUpdate) return@collect
                        if (userRooms.find { it.id == update.roomId } == null) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }

            val gameUpdateHandler = async {
                matchRepository.updates
                    .collect { update ->
                        if (update is UpdateModel.GameStateUpdate && userRooms.find { it.id == update.roomId } == null) return@collect
                        if (update is UpdateModel.UsersUpdate && userRooms.find { it.id == update.roomId } == null) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }
            awaitAll(roomUpdateHandler, gameUpdateHandler)
        }
    }
}