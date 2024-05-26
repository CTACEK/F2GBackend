package com.ctacek.f2g.api.v1.routing

import com.ctacek.f2g.api.v1.requests.users.UpdateUserRequest
import com.ctacek.f2g.api.v1.requests.users.auth.LoginViaUsernameRequest
import com.ctacek.f2g.api.v1.requests.users.auth.RefreshTokenRequest
import com.ctacek.f2g.api.v1.requests.users.auth.SignUpViaUsernameRequest
import com.ctacek.f2g.domain.entities.UserDTO
import com.ctacek.f2g.domain.useCases.UseCases
import com.ctacek.f2g.domain.useCases.rooms.GetUserRoomsUseCase
import com.ctacek.f2g.domain.useCases.users.DeleteUserUseCase
import com.ctacek.f2g.domain.useCases.users.GetUserDetailsUseCase
import com.ctacek.f2g.domain.useCases.users.UpdateUserUseCase
import com.ctacek.f2g.domain.useCases.users.auth.LoginViaUsernameUseCase
import com.ctacek.f2g.domain.useCases.users.auth.RefreshTokenUseCase
import com.ctacek.f2g.domain.useCases.users.auth.SignUpViaUsernameUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureUserRoutes(
    useCases: UseCases,
) {
    configureAuthRoutes(useCases)

    get("/avatars") {
        val res = useCases.getAvailableAvatarsUseCase()
        call.respond(HttpStatusCode.OK, res)
    }

    route("/user") {
        authenticate {
            get("/rooms") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@get
                }

                when (val res = useCases.getUserRoomsUseCase(userId)) {
                    is GetUserRoomsUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK, res.rooms)
                        return@get
                    }

                    GetUserRoomsUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not found")
                        return@get
                    }
                }
            }
        }

        authenticate {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@get
                }
                when (val res = useCases.getUserDetailsUseCase(userId)) {
                    GetUserDetailsUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@get
                    }

                    is GetUserDetailsUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK, res.user)
                        return@get
                    }

                    GetUserDetailsUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not found")
                        return@get
                    }
                }
            }
        }
        authenticate {
            delete {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@delete
                }

                when (useCases.deleteUserUseCase(userId)) {
                    DeleteUserUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@delete
                    }

                    DeleteUserUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@delete
                    }

                    DeleteUserUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not found")
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

                val userUpdate = call.receiveNullable<UpdateUserRequest>()?.let {
                    UserDTO.UpdateUser(
                        username = it.username,
                        avatar = it.avatar,
                    )
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }

                when (useCases.updateUserUseCase(userId, userUpdate)) {
                    UpdateUserUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@patch
                    }

                    UpdateUserUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@patch
                    }

                    UpdateUserUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not found")
                        return@patch
                    }

                    UpdateUserUseCase.Result.AvatarNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Avatar not found")
                        return@patch
                    }
                }
            }
        }
    }
}

private fun Route.configureAuthRoutes(useCases: UseCases) {
    route("/auth") {
        post("/username/register") { // Register user
            val clientId = call.request.headers["client-id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "No client id provided")
                return@post
            }

            val registerRequest = call.receiveNullable<SignUpViaUsernameRequest>()?.let {
                UserDTO.UserSignUp(
                    username = it.username,
                    password = it.password,
                    clientId = clientId,
                    avatar = it.avatar,
                )
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            when (val res = useCases.signUpViaUsernameUseCase(registerRequest)) {
                SignUpViaUsernameUseCase.Result.Failed -> {
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }

                is SignUpViaUsernameUseCase.Result.Successful -> {
                    call.respond(HttpStatusCode.OK, res.tokenPair)
                    return@post
                }

                SignUpViaUsernameUseCase.Result.UserAlreadyExists -> {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                    return@post
                }

                SignUpViaUsernameUseCase.Result.AvatarNotFound -> {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
            }
        }

        post("/username/login") {
            val clientId = call.request.headers["client-id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "No client id provided")
                return@post
            }
            val (username, password) = call.receiveNullable<LoginViaUsernameRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            when (
                val res = useCases.loginViaUsernameUseCase(
                    username = username,
                    password = password,
                    clientId = clientId,
                )
            ) {
                LoginViaUsernameUseCase.Result.Failed -> {
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }

                is LoginViaUsernameUseCase.Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.tokenPair)
                    return@post
                }

                LoginViaUsernameUseCase.Result.Forbidden -> {
                    call.respond(HttpStatusCode.Forbidden, "Wrong password or username")
                    return@post
                }
            }
        }

        post("/refresh") {
            val oldRefreshToken = call.receiveNullable<RefreshTokenRequest>()?.oldRefreshToken ?: run {
                call.respond(HttpStatusCode.BadRequest, "No refresh token provided")
                return@post
            }

            when (
                val res = useCases.refreshTokenUseCase(
                    oldRefreshToken = oldRefreshToken,
                )
            ) {
                is RefreshTokenUseCase.Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.tokenPair)
                    return@post
                }

                RefreshTokenUseCase.Result.NoRefreshTokenFound -> {
                    call.respond(HttpStatusCode.BadRequest, "Invalid refresh token")
                    return@post
                }

                RefreshTokenUseCase.Result.RefreshTokenExpired -> {
                    call.respond(HttpStatusCode.BadRequest, "Refresh token expired")
                    return@post
                }

                RefreshTokenUseCase.Result.Forbidden -> {
                    call.respond(HttpStatusCode.Forbidden, "Forbidden")
                    return@post
                }

                RefreshTokenUseCase.Result.Failed -> {
                    call.respond(HttpStatusCode.InternalServerError)
                    return@post
                }
            }
        }
    }
}