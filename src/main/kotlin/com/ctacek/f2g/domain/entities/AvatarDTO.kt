package com.ctacek.f2g.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class AvatarDTO(
    val id: Int,
    val image: String,
)