package com.ctacek.f2g.domain.useCases.users.auth

import com.ctacek.f2g.domain.entities.UserDTO
import com.ctacek.f2g.domain.repositories.UsersRepository
import com.ctacek.f2g.security.jwt.hashing.HashingService
import com.ctacek.f2g.security.jwt.token.*
import com.ctacek.f2g.utils.getRandomUserID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignUpViaUsernameUseCase : KoinComponent {

    private val usersRepository: UsersRepository by inject()
    private val tokenService: TokenService by inject()
    private val hashingService: HashingService by inject()
    private val tokenConfig: TokenConfig by inject()

    sealed interface Result {
        data class Successful(val tokenPair: TokenPair) : Result
        object Failed : Result
        object UserAlreadyExists : Result
        object AvatarNotFound : Result
    }

    suspend operator fun invoke(user: UserDTO.UserSignUp): Result {
        if (usersRepository.getUserByUsername(user.username) != null) return Result.UserAlreadyExists
        val userId = getRandomUserID()
        val tokenPair = tokenService.generateTokenPair(tokenConfig, TokenClaim("userId", userId))

        val avatar = usersRepository.getAvatarById(user.avatar) ?: return Result.AvatarNotFound

        val resUser = UserDTO.User(
            userId = userId,
            username = user.username,
            passwordHash = hashingService.generateHash(user.password),
            authProvider = "local",
            avatar = avatar,
        )
        val registerUserResult = usersRepository.registerUser(resUser)
        val createUserRefreshTokenResult = usersRepository.createRefreshToken(
            userId = userId,
            clientId = user.clientId,
            refreshToken = RefreshToken(
                token = tokenPair.refreshToken.token,
                expiresAt = tokenPair.refreshToken.expiresAt,
            ),
        )

        return if (registerUserResult && createUserRefreshTokenResult) Result.Successful(tokenPair) else Result.Failed
    }
}