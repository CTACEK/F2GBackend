package com.ctacek.f2g.api.v1.responses

import kotlinx.serialization.Serializable

@Serializable
data class MovieCollection(
    val films: List<Movie>,
)

@Serializable
data class Movie(
    val filmId: Int,
    val nameRu: String,
    val nameEn: String?,
    val posterUrl: String,
)