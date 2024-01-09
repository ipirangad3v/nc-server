@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package digital.thon.nc.application

import com.auth0.jwt.JWTVerifier
import digital.thon.nc.application.plugins.configureKoin
import digital.thon.nc.application.plugins.configureMonitoring
import digital.thon.nc.application.plugins.configureRouting
import digital.thon.nc.application.plugins.configureSecurity
import digital.thon.nc.application.plugins.configureSerialization
import digital.thon.nc.data.dao.UserDao
import digital.thon.nc.data.database.DatabaseProviderContract
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

// application.conf references the main function. This annotation prevents the IDE from marking it as unused.
@Suppress("unused")
fun Application.module() {
    val databaseProvider by inject<DatabaseProviderContract>()
    val userDao by inject<UserDao>()
    val jwtVerifier by inject<JWTVerifier>()

    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureSecurity(userDao, jwtVerifier)
    configureRouting()

    // initialize database
    databaseProvider.init()
}
