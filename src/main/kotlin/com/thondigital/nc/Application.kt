package com.thondigital.nc

import com.thondigital.nc.plugins.configureHTTP
import com.thondigital.nc.plugins.configureRouting
import com.thondigital.nc.plugins.configureSecurity
import com.thondigital.nc.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
