package com.ctacek.f2g.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDate

interface RoomEntity : Entity<RoomEntity> {
    companion object : Entity.Factory<RoomEntity>()

    var id: String
    var name: String
    var date: LocalDate?
    var ownerId: String
    var playableOwner: Boolean
    var maxPrice: Int?
    var gameStarted: Boolean
}

object Rooms : Table<RoomEntity>("rooms") {
    var id = text("id").primaryKey().bindTo(RoomEntity::id)
    var name = text("name").bindTo(RoomEntity::name)
    var date = date("date").bindTo(RoomEntity::date)
    var ownerId = text("owner_id").bindTo(RoomEntity::ownerId)
    var playableOwner = boolean("playable_owner").bindTo(RoomEntity::playableOwner)
    var maxPrice = int("max_price").bindTo(RoomEntity::maxPrice)
    var gameStarted = boolean("game_started").bindTo { it.gameStarted }
}