package com.ctacek.f2g.data.mappers

import com.ctacek.f2g.data.entities.RoomEntity
import com.ctacek.f2g.domain.entities.RoomDTO

fun RoomEntity.mapToRoom(): RoomDTO.Room = RoomDTO.Room(
    id = this.id,
    shortName = this.shortName,
    name = this.name,
    date = this.date,
    ownerId = this.ownerId,
    gameStarted = this.gameStarted,
    membersCount = 1,
)