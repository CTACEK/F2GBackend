package com.ctacek.f2g.data.repositories.movie

import com.ctacek.f2g.domain.entities.MovieCollectionDTO
import com.ctacek.f2g.domain.repositories.MovieRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class RemoteMovieRepository(
    private val client: HttpClient,
    private val kinopoiskApiToken: String,
) : MovieRepository {
    private val baseUrl = "https://kinopoiskapiunofficial.tech/api/v2.2"

    private val filmsCollection = "$baseUrl/films/collections?type=TOP_POPULAR_ALL&page=1"

    override suspend fun getRandomMovies(count: Int): MovieCollectionDTO {
        return client.get(filmsCollection) {
            headers {
                append("X-API-KEY", kinopoiskApiToken)
                contentType(ContentType.Application.Json)
            }
        }.body()
    }
}