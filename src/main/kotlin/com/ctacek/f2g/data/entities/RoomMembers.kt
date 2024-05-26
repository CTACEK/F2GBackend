package com.ctacek.f2g.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.text

interface RoomMember : Entity<RoomMember> {
    companion object : Entity.Factory<RoomMember>()

    var roomEntityId: RoomEntity
    var userId: User
    var accepted: Boolean
}

object RoomMembers : Table<RoomMember>("room_members") {
    var roomId = text("room_id").references(Rooms) { it.roomEntityId }
    var userId = text("user_id").references(Users) { it.userId }
    var accepted = boolean("accepted").bindTo(RoomMember::accepted)
}