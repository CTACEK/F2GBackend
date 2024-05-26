package com.ctacek.f2g.data.mappers

import com.ctacek.f2g.data.entities.User
import com.ctacek.f2g.domain.entities.UserDTO

fun User.mapToUser(): UserDTO.User = UserDTO.User(
    userId = this.userId,
    username = this.username,
    passwordHash = this.passwordHash,
    authProvider = this.authProvider,
    avatar = this.avatar.image,
)