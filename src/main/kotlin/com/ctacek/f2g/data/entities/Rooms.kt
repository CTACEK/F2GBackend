package com.ctacek.f2g.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDate

interface RoomEntity : Entity<RoomEntity> {
    companion object : Entity.Factory<RoomEntity>()

    var id: String
    var name: String
    var shortName: String
    var date: LocalDate?
    var ownerId: String
    var gameStarted: Boolean
}

object Rooms : Table<RoomEntity>("rooms") {
    var id = text("id").primaryKey().bindTo(RoomEntity::id)
    var shortName = text("short_name").primaryKey().bindTo(RoomEntity::shortName)
    var name = text("name").bindTo(RoomEntity::name)
    var date = date("date").bindTo(RoomEntity::date)
    var ownerId = text("owner_id").bindTo(RoomEntity::ownerId)
    var gameStarted = boolean("game_started").bindTo { it.gameStarted }
}