package com.ctacek.f2g.domain.useCases.users

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.entities.UserDTO
import com.ctacek.f2g.domain.repositories.UsersRepository

class GetUserDetailsUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        data class Successful(val user: UserDTO.UserInfo) : Result
        object Failed : Result
        object UserNotFound : Result
    }

    suspend operator fun invoke(
        userId: String,
    ): Result {
        val user = usersRepository.getUserByID(userId) ?: return Result.UserNotFound
        return Result.Successful(user)
    }
}