package com.ctacek.f2g.di

import com.ctacek.f2g.data.repositories.game.PostgresMatchRepository
import com.ctacek.f2g.data.repositories.movie.RemoteMovieRepository
import com.ctacek.f2g.data.repositories.rooms.PostgresRoomsRepository
import com.ctacek.f2g.data.repositories.users.PostgresUsersRepository
import com.ctacek.f2g.domain.repositories.MatchRepository
import com.ctacek.f2g.domain.repositories.MovieRepository
import com.ctacek.f2g.domain.repositories.RoomsRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.domain.services.OneSignalService
import com.ctacek.f2g.domain.useCases.UseCases
import com.ctacek.f2g.security.jwt.hashing.BcryptHashingService
import com.ctacek.f2g.security.jwt.hashing.HashingService
import com.ctacek.f2g.security.jwt.token.JwtTokenService
import com.ctacek.f2g.security.jwt.token.TokenConfig
import com.ctacek.f2g.security.jwt.token.TokenService
import com.ctacek.f2g.services.OneSignalServiceImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.ktorm.database.Database

@OptIn(ExperimentalSerializationApi::class)
val appModule = module {
    single {
        Database.connect(
            url = System.getenv("DATABASE_CONNECTION_STRING"),
            driver = "org.postgresql.Driver",
            user = System.getenv("POSTGRES_NAME"),
            password = System.getenv("POSTGRES_PASSWORD"),
        )
    }

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = false
                        explicitNulls = false
                    },
                )
            }
        }
    }

    single<OneSignalService> { OneSignalServiceImpl(get()) }

    single<UsersRepository> { PostgresUsersRepository(get()) }
    single<RoomsRepository> { PostgresRoomsRepository(get()) }
    single<MatchRepository> { PostgresMatchRepository(get()) }
    single<MovieRepository> {
        RemoteMovieRepository(
            client = get(),
            kinopoiskApiToken = System.getenv("KINOPOISK_API_TOKEN"),
        )
    }

    single {
        TokenConfig(
            issuer = System.getenv("JWT_ISSUER"),
            audience = System.getenv("JWT_AUDIENCE"),
            accessLifetime = System.getenv("JWT_ACCESS_LIFETIME").toLong(),
            refreshLifetime = System.getenv("JWT_REFRESH_LIFETIME").toLong(),
            secret = System.getenv("JWT_SECRET"),
        )
    }

    single<TokenService> { JwtTokenService() }

    single<HashingService> { BcryptHashingService() }

    single { UseCases() }
}