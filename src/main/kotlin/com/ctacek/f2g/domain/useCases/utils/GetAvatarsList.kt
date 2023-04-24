package com.ctacek.f2g.domain.useCases.utils

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ctacek.f2g.domain.entities.AvatarDTO
import com.ctacek.f2g.domain.repositories.UsersRepository

class GetAvatarsList : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    suspend operator fun invoke(): List<AvatarDTO> =
        usersRepository.getAvailableAvatars()
}