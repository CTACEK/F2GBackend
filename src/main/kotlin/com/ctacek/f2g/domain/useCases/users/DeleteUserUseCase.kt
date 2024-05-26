package com.ctacek.f2g.domain.useCases.users

import com.ctacek.f2g.domain.repositories.UsersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeleteUserUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object UserNotExists : Result
    }

    suspend operator fun invoke(userId: String): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        return if (usersRepository.deleteUserByID(userId)) Result.Successful else Result.Failed
    }
}