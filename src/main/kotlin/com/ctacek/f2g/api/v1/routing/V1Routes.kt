package com.ctacek.f2g.api.v1.routing

import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import com.ctacek.f2g.domain.repositories.GameRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.domain.useCases.UseCases

fun Route.v1Routes() {
    route("/v1") {
        val useCases by inject<UseCases>()
        val roomsRepository by inject<RoomsRepository>()
        val gameRepository by inject<GameRepository>()
        val usersRepository by inject<UsersRepository>()

        configureUserRoutes(useCases)
        configureRoomsRoutes(useCases)
        configureGameRoutes(
            useCases = useCases,
            usersRepository = usersRepository,
            roomsRepository = roomsRepository,
            gameRepository = gameRepository,
        )
    }
}