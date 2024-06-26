package com.ctacek.f2g.api.v1.routing

import com.ctacek.f2g.api.v1.requests.match.JoinRoomRequest
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.domain.useCases.UseCases
import com.ctacek.f2g.domain.useCases.game.GetGameInfoUseCase
import com.ctacek.f2g.domain.useCases.game.JoinRoomUseCase
import com.ctacek.f2g.domain.useCases.game.StartGameUseCase
import com.ctacek.f2g.domain.useCases.game.StopGameUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureMatchRoutes(
    useCases: UseCases,
    usersRepository: UsersRepository,
    roomsRepository: RoomsRepository,
    matchRepository: MatchRepository,
) {
    route("/match") {
        webSockets(usersRepository, roomsRepository, matchRepository)

        authenticate {
            post("/join") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val request = call.receiveNullable<JoinRoomRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val res = useCases.joinRoomUseCase(
                    userId = userId,
                    roomId = request.roomId,
                )
                when (res) {
                    JoinRoomUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    JoinRoomUseCase.Result.GameAlreadyStarted -> {
                        call.respond(HttpStatusCode.Conflict, "Game already started")
                        return@post
                    }

                    JoinRoomUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    JoinRoomUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    JoinRoomUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    JoinRoomUseCase.Result.UserAlreadyInRoom -> {
                        call.respond(HttpStatusCode.Conflict, "User already in room")
                        return@post
                    }
                }
            }
        }

        authenticate {
            post("/start") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@post
                }
                val res = useCases.startGameUseCase(
                    userId = userId,
                    roomId = id,
                )

                when (res) {
                    StartGameUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    StartGameUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    StartGameUseCase.Result.GameAlreadyStarted -> {
                        call.respond(HttpStatusCode.Conflict, "Game already started")
                        return@post
                    }

                    StartGameUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    StartGameUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    StartGameUseCase.Result.NotEnoughPlayers -> {
                        call.respond(HttpStatusCode.BadRequest, "Not enough users to start playing")
                        return@post
                    }

                    StartGameUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    StartGameUseCase.Result.ActiveRequests -> {
                        call.respond(HttpStatusCode.NotAcceptable, "You have active requests")
                        return@post
                    }
                }
            }
        }
        authenticate {
            post("/stop") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@post
                }
                val res = useCases.stopGameUseCase(
                    userId = userId,
                    roomId = id,
                )
                when (res) {
                    StopGameUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    StopGameUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    StopGameUseCase.Result.GameAlreadyStopped -> {
                        call.respond(HttpStatusCode.Conflict, "Game already stopped")
                        return@post
                    }

                    StopGameUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    StopGameUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    StopGameUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                }
            }
        }
        authenticate {
            get("/info") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@get
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room name")
                    return@get
                }
                val res = useCases.getGameInfoUseCase(
                    userId = userId,
                    roomId = id,
                )

                when (res) {
                    GetGameInfoUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@get
                    }

                    GetGameInfoUseCase.Result.RoomNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@get
                    }

                    GetGameInfoUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@get
                    }

                    is GetGameInfoUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK, res.info)
                        return@get
                    }
                }
            }
        }
    }
}