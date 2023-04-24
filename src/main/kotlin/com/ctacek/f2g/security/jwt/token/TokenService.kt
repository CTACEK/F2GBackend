package com.ctacek.f2g.security.jwt.token

interface TokenService {
    fun generateTokenPair(config: TokenConfig, vararg claims: TokenClaim): TokenPair
}