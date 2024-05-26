package com.ctacek.f2g.data.mappers

import com.ctacek.f2g.api.v1.responses.Movie
import com.ctacek.f2g.api.v1.responses.MovieCollection
import com.ctacek.f2g.domain.entities.MovieCollectionDTO
import com.ctacek.f2g.domain.entities.MovieDTO

fun MovieCollectionDTO.mapToMovieCollection() = MovieCollection(
    films = items.map { it.mapToMovie() },
)

fun MovieDTO.mapToMovie() = Movie(
    filmId = kinopoiskId,
    nameRu = nameRu,
    nameEn = nameEn,
    posterUrl = posterUrl,
)
