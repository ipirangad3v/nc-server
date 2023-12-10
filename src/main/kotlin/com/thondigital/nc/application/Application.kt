@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package com.thondigital.nc.application

import com.auth0.jwt.JWTVerifier
import com.thondigital.nc.application.plugins.configureKoin
import com.thondigital.nc.application.plugins.configureMonitoring
import com.thondigital.nc.application.plugins.configureRouting
import com.thondigital.nc.application.plugins.configureSecurity
import com.thondigital.nc.application.plugins.configureSerialization
import com.thondigital.nc.data.dao.UserDao
import com.thondigital.nc.data.database.DatabaseProviderContract
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

// application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val databaseProvider by inject<DatabaseProviderContract>()
    val userDao by inject<UserDao>()
    val jwtVerifier by inject<JWTVerifier>()

    val client =
        HttpClient(CIO) {
        }
    val apiKey = System.getenv("ONESIGNAL_API_KEY")

    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureSecurity(userDao, jwtVerifier)
    configureRouting(client, apiKey)

    // initialize database
    databaseProvider.init()
}
