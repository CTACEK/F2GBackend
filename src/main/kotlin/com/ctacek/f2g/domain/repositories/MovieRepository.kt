package com.ctacek.f2g.domain.repositories

import com.ctacek.f2g.domain.entities.MovieCollectionDTO

interface MovieRepository {
    suspend fun getRandomMovies(count: Int): MovieCollectionDTO
}