package com.thondigital.nc.application.plugins

import com.thondigital.nc.application.router.authApi
import com.thondigital.nc.application.router.blogApi
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(
    httpClient: HttpClient,
    apiKey: String,
) {
    routing {
        authApi()
        blogApi(httpClient, apiKey)
    }
}
