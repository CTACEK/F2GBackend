package com.ctacek.f2g.security.jwt.hashing

import at.favre.lib.crypto.bcrypt.BCrypt

interface HashingService {
    fun generateHash(password: String): String

    fun verify(password: String, hash: String): BCrypt.Result
}