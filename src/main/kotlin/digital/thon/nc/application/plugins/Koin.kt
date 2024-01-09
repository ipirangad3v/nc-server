package digital.thon.nc.application.plugins

import com.auth0.jwt.JWTVerifier
import digital.thon.nc.application.auth.JWTController
import digital.thon.nc.application.auth.PasswordEncryptor
import digital.thon.nc.application.auth.PasswordEncryptorContract
import digital.thon.nc.application.auth.TokenProvider
import digital.thon.nc.application.controller.di.ControllerModule
import digital.thon.nc.data.di.DaoModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger(level = org.koin.core.logger.Level.ERROR) // This params are the workaround itself
        modules(
            module {
                single<JWTVerifier> { JWTController.verifier }
                single<TokenProvider> { JWTController }
                single<PasswordEncryptorContract> { PasswordEncryptor }
            },
            DaoModule.koinBeans,
            ControllerModule.koinBeans,
        )
    }
}
