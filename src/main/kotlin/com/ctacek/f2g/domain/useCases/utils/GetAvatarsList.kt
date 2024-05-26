package com.ctacek.f2g.domain.useCases.utils

import com.ctacek.f2g.domain.entities.AvatarDTO
import com.ctacek.f2g.domain.repositories.UsersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetAvatarsList : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    suspend operator fun invoke(): List<AvatarDTO> =
        usersRepository.getAvailableAvatars()
}