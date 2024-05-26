package com.ctacek.f2g.api.v1.routing

import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.domain.useCases.UseCases
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.v1Routes() {
    route("/v1") {
        val useCases by inject<UseCases>()
        val roomsRepository by inject<RoomsRepository>()
        val matchRepository by inject<MatchRepository>()
        val usersRepository by inject<UsersRepository>()

        configureUserRoutes(useCases)
        configureRoomRoutes(useCases)
        configureMovieRoutes(useCases)
        configureMatchRoutes(
            useCases = useCases,
            usersRepository = usersRepository,
            roomsRepository = roomsRepository,
            matchRepository = matchRepository,
        )
    }
}