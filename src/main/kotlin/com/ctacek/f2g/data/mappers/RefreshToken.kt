package com.ctacek.f2g.data.mappers

import com.ctacek.f2g.data.entities.RefreshToken
import com.ctacek.f2g.domain.entities.UserDTO

fun RefreshToken.toRefreshTokenInfo(): UserDTO.RefreshTokenInfo {
    return UserDTO.RefreshTokenInfo(
        userId = this.userId,
        clientId = this.clientId,
        token = this.refreshToken,
        expiresAt = this.expiresAt,
    )
}

fun com.ctacek.f2g.security.jwt.token.RefreshToken.toDataRefreshToken(
    userId: String,
    clientId: String,
): RefreshToken {
    val refreshToken = this
    return RefreshToken {
        this.userId = userId
        this.clientId = clientId
        this.refreshToken = refreshToken.token
        this.expiresAt = refreshToken.expiresAt
    }
}