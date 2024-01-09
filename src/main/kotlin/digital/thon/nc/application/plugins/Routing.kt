package digital.thon.nc.application.plugins

import digital.thon.nc.application.router.authApi
import digital.thon.nc.application.router.eventApi
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        authApi()
        eventApi()
    }
}
