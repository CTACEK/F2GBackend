package com.ctacek.f2g.security.jwt.token

data class TokenClaim(
    val name: String,
    val value: String,
)