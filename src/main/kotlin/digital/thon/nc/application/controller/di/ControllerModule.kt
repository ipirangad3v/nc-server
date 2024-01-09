package digital.thon.nc.application.controller.di

import digital.thon.nc.application.controller.auth.AuthController
import digital.thon.nc.application.controller.auth.DefaultAuthController
import org.koin.dsl.module

object ControllerModule {
    val koinBeans =
        module {
            single<AuthController> { DefaultAuthController() }
        }
}