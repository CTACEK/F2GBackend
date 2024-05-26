package com.ctacek.f2g.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class MovieCollectionDTO(
    val items: List<MovieDTO>,
)

@Serializable
data class MovieDTO(
    val kinopoiskId: Int,
    val nameRu: String,
    val nameEn: String?,
    val posterUrl: String,
)