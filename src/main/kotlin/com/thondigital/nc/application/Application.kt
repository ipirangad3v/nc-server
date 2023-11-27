@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package com.thondigital.nc.application

import com.auth0.jwt.interfaces.JWTVerifier
import com.thondigital.nc.application.auth.firebase.FirebaseAdmin
import com.thondigital.nc.application.plugins.configureKoin
import com.thondigital.nc.application.plugins.configureMonitoring
import com.thondigital.nc.application.plugins.configureRouting
import com.thondigital.nc.application.plugins.configureSecurity
import com.thondigital.nc.application.plugins.configureSerialization
import com.thondigital.nc.data.dao.UserDao
import com.thondigital.nc.data.database.DatabaseProviderContract
import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import org.koin.core.annotation.KoinReflectAPI
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@OptIn(KoinReflectAPI::class) // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val databaseProvider by inject<DatabaseProviderContract>()
    val userDao by inject<UserDao>()
    val jwtVerifier by inject<JWTVerifier>()

    val client =
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    val dotenv = dotenv()
    val apiKey = dotenv["ONESIGNAL_API_KEY"]

    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureSecurity(userDao, jwtVerifier)
    configureRouting(client, apiKey)

    // initialize database
    databaseProvider.init()

    // initialize Firebase Admin SDK
    FirebaseAdmin.init()
}
