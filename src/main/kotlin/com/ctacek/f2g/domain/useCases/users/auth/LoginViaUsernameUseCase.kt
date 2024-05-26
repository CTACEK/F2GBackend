package com.ctacek.f2g.domain.useCases.users.auth

import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.security.jwt.hashing.HashingService
import com.ctacek.f2g.security.jwt.token.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViaUsernameUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val hashingService: HashingService by inject()
    private val tokenService: TokenService by inject()
    private val tokenConfig: TokenConfig by inject()

    sealed interface Result {
        data class Success(val tokenPair: TokenPair) : Result
        object Forbidden : Result
        object Failed : Result
    }

    suspend operator fun invoke(username: String, password: String, clientId: String): Result {
        val user = usersRepository.getUserByUsername(username) ?: return Result.Forbidden
        val passwordVerificationResult = hashingService.verify(password, user.passwordHash ?: return Result.Forbidden)
        if (!passwordVerificationResult.verified) return Result.Forbidden
        val tokenPair = tokenService.generateTokenPair(tokenConfig, TokenClaim("userId", user.userId))

        val isTokenExists = usersRepository.getRefreshToken(user.userId, clientId) != null

        val tokenUpdateResult = when {
            isTokenExists -> usersRepository.updateUserRefreshToken(
                userId = user.userId,
                clientId = clientId,
                newRefreshToken = tokenPair.refreshToken.token,
                refreshTokenExpiration = tokenPair.refreshToken.expiresAt,
            )

            else -> usersRepository.createRefreshToken(
                userId = user.userId,
                clientId = clientId,
                refreshToken = RefreshToken(
                    token = tokenPair.refreshToken.token,
                    expiresAt = tokenPair.refreshToken.expiresAt,
                ),
            )
        }

        if (tokenUpdateResult) {
            return Result.Success(tokenPair)
        }
        return Result.Failed
    }
}