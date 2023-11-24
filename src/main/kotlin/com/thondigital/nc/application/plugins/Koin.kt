package com.thondigital.nc.application.plugins

import com.auth0.jwt.interfaces.JWTVerifier
import com.thondigital.nc.application.auth.JWTController
import com.thondigital.nc.application.auth.PasswordEncryptor
import com.thondigital.nc.application.auth.PasswordEncryptorContract
import com.thondigital.nc.application.auth.TokenProvider
import com.thondigital.nc.application.controller.di.ControllerModule
import com.thondigital.nc.data.di.DaoModule
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.core.annotation.KoinReflectAPI
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

@KoinReflectAPI
fun Application.configureKoin() {
    install(feature = Koin) {
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
