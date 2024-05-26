package com.ctacek.f2g.api.v1.routing

import com.ctacek.f2g.api.v1.requests.rooms.CreateRoomRequest
import com.ctacek.f2g.api.v1.requests.rooms.UpdateRoomRequest
import com.ctacek.f2g.domain.entities.RoomDTO.RoomUpdate
import com.ctacek.f2g.domain.useCases.UseCases
import com.ctacek.f2g.domain.useCases.game.JoinRoomUseCase
import com.ctacek.f2g.domain.useCases.rooms.CreateRoomUseCase
import com.ctacek.f2g.domain.useCases.rooms.DeleteRoomUseCase
import com.ctacek.f2g.domain.useCases.rooms.GetRoomDetailsUseCase
import com.ctacek.f2g.domain.useCases.rooms.UpdateRoomUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureRoomRoutes(
    useCases: UseCases,
) {
    route("/room") {
        authenticate {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@get
                }

                val roomId = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@get
                }

                when (val res = useCases.getRoomDetailsUseCase(userId, roomId)) {
                    GetRoomDetailsUseCase.Result.RoomNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@get
                    }

                    GetRoomDetailsUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@get
                    }

                    GetRoomDetailsUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@get
                    }

                    is GetRoomDetailsUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK, res.room)
                        return@get
                    }
                }
            }
        }
        authenticate {
            post { // Create room
                val request = call.receiveNullable<CreateRoomRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }
                val res = useCases.createRoomUseCase(
                    userId = userId,
                    roomName = request.username,
                    date = request.date,
                )
                when (res) {
                    CreateRoomUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    is CreateRoomUseCase.Result.Successful -> {
                        val joinRequest = useCases.joinRoomUseCase(
                            userId = userId,
                            roomId = res.room.id,
                        )
                        if (joinRequest is JoinRoomUseCase.Result.Successful) {
                            call.respond(HttpStatusCode.OK, res.room)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        }
                        return@post
                    }

                    CreateRoomUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }
                }
            }
        }
        authenticate {
            delete {
                val roomId = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@delete
                }
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@delete
                }
                when (
                    useCases.deleteRoomUseCase(
                        userId = userId,
                        roomId = roomId,
                    )
                ) {
                    DeleteRoomUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@delete
                    }

                    DeleteRoomUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@delete
                    }

                    DeleteRoomUseCase.Result.RoomNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@delete
                    }

                    DeleteRoomUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@delete
                    }

                    DeleteRoomUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User Not Exists")
                        return@delete
                    }
                }
            }
        }

        authenticate {
            patch {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@patch
                }

                val roomId = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@patch
                }
                val roomUpdate = call.receiveNullable<UpdateRoomRequest>()?.let {
                    RoomUpdate(
                        name = it.name,
                        date = it.date,
                    )
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }

                when (
                    useCases.updateRoomUseCase(
                        userId = userId,
                        roomId = roomId,
                        roomUpdate = roomUpdate,
                    )
                ) {
                    UpdateRoomUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@patch
                    }

                    UpdateRoomUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@patch
                    }

                    UpdateRoomUseCase.Result.RoomNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@patch
                    }

                    UpdateRoomUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@patch
                    }

                    UpdateRoomUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@patch
                    }
                }
            }
        }
    }
}