package com.ctacek.f2g.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    var userId: String
    var username: String
    var passwordHash: String?
    var authProvider: String
    var avatar: Avatar
}

object Users : Table<User>("users") {
    var userId = text("user_id").primaryKey().bindTo(User::userId)
    var username = text("username").bindTo(User::username)
    var passwordHash = text("password_hash").bindTo(User::passwordHash)
    val authProvider = text("auth_provider").bindTo(User::authProvider)
    var avatar = int("avatar").references(Avatars) { it.avatar }
}