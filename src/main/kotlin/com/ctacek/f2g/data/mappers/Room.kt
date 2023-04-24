package com.ctacek.f2g.data.mappers

import com.ctacek.f2g.data.entities.RoomEntity
import com.ctacek.f2g.domain.entities.RoomDTO

fun RoomEntity.mapToRoom(): RoomDTO.Room = RoomDTO.Room(
    name = this.name,
    id = this.id,
    date = this.date,
    ownerId = this.ownerId,
    playableOwner = this.playableOwner,
    maxPrice = this.maxPrice,
    gameStarted = this.gameStarted,
    membersCount = 1,
)