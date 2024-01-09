package digital.thon.nc.application.controller.di

import digital.thon.nc.application.controller.auth.AuthController
import digital.thon.nc.application.controller.auth.DefaultAuthController
import digital.thon.nc.application.controller.event.DefaultEventController
import digital.thon.nc.application.controller.event.EventController
import org.koin.dsl.module

object ControllerModule {
    val koinBeans =
        module {
            single<AuthController> { DefaultAuthController() }
            single<EventController> { DefaultEventController() }
        }
}
