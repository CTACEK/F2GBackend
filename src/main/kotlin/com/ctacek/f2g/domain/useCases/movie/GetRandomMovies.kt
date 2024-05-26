package com.ctacek.f2g.domain.useCases.movie

import com.ctacek.f2g.api.v1.responses.MovieCollection
import com.ctacek.f2g.data.mappers.mapToMovieCollection
import com.ctacek.f2g.domain.repositories.MovieRepository
import com.ctacek.f2g.domain.repositories.UsersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetRandomMovies : KoinComponent {
    private val movieRepository: MovieRepository by inject()
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        data class Successful(val movies: MovieCollection) : Result
        object UserNotExists : Result
        object Forbidden : Result
    }

    suspend operator fun invoke(
        userId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists

        val movies = movieRepository.getRandomMovies(LIMIT).mapToMovieCollection()

        return Result.Successful(movies)
    }

    companion object {
        private const val LIMIT = 20
    }
}