package com.ctacek.f2g.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface UserDTO {
    @Serializable
    data class User(
        @SerialName("user_id") val userId: String,
        val username: String,
        val passwordHash: String?,
        @SerialName("auth_provider") val authProvider: String,
        val avatar: String,
    ) : UserDTO

    @Serializable
    data class UserSignUp(
        val username: String,
        val password: String,
        @SerialName("client_id") val clientId: String,
        val avatar: Int,
    ) : UserDTO

    @Serializable
    data class UserInfo(
        @SerialName("user_id") val userId: String,
        @SerialName("client_ids") val clientIds: List<String>? = null,
        val username: String,
        val avatar: String,
    ) : UserDTO

    @Serializable
    data class UserRoomInfo(
        @SerialName("user_id") val userId: String,
        val username: String,
        val avatar: String,
    ) : UserDTO

    data class RefreshTokenInfo(
        val userId: String,
        val clientId: String,
        val token: String,
        val expiresAt: Long,
    ) : UserDTO

    data class UpdateUser(
        val username: String,
        val avatar: Int?,
    ) : UserDTO
}