package com.ctacek.f2g.services

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.ctacek.f2g.domain.entities.Notification
import com.ctacek.f2g.domain.services.OneSignalService

class OneSignalServiceImpl(
    private val client: HttpClient,
) : OneSignalService {
    override suspend fun sendNotification(notification: Notification): Boolean {
        return try {
            println(Json.encodeToString(notification))
            val res = client.post(OneSignalService.BASE_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Basic ${OneSignalService.API_KEY}")
                setBody(notification)
            }

            println(res.bodyAsText())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}