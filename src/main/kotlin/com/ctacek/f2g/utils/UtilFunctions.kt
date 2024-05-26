package com.ctacek.f2g.utils

import java.util.*

fun getRandomRoomID(): String = UUID.randomUUID().toString().subSequence(0..6).toString()
fun getRandomUserID(): String = UUID.randomUUID().toString().subSequence(0..7).toString()
fun getShortRoomName() = (1..5).map { ('a'..'z') + ('A'..'Z') }.flatten().shuffled().joinToString()