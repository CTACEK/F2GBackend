package com.ctacek.f2g.api.v1.routing

import com.ctacek.f2g.domain.useCases.UseCases
import com.ctacek.f2g.domain.useCases.movie.GetRandomMovies
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureMovieRoutes(
    useCases: UseCases,
) {
    route("/movies/random") {
        authenticate {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@get
                }
                when (val res = useCases.getRandomMovies(userId)) {
                    GetRandomMovies.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@get
                    }
                    GetRandomMovies.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@get
                    }
                    is GetRandomMovies.Result.Successful -> {
                        call.respond(HttpStatusCode.OK, res.movies)
                        return@get
                    }
                }
            }
        }
    }
}