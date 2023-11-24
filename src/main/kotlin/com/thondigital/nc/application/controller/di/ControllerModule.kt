package com.thondigital.nc.application.controller.di

import com.thondigital.nc.application.controller.auth.AuthController
import com.thondigital.nc.application.controller.auth.DefaultAuthController
import com.thondigital.nc.application.controller.blog.BlogController
import com.thondigital.nc.application.controller.blog.DefaultBlogController
import org.koin.dsl.module

object ControllerModule {
    val koinBeans =
        module {
            single<AuthController> { DefaultAuthController() }
            single<BlogController> { DefaultBlogController() }
        }
}
