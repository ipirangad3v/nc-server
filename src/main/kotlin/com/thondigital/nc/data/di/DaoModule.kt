package com.thondigital.nc.data.di

import com.thondigital.nc.data.dao.BlogsDao
import com.thondigital.nc.data.dao.TokenDao
import com.thondigital.nc.data.dao.UserDao
import com.thondigital.nc.data.database.DatabaseProvider
import com.thondigital.nc.data.database.DatabaseProviderContract
import com.thondigital.nc.data.database.table.Blogs
import com.thondigital.nc.data.database.table.Tokens
import com.thondigital.nc.data.database.table.Users
import org.koin.dsl.module

object DaoModule {
    val koinBeans =
        module {
            single<TokenDao> { Tokens }
            single<UserDao> { Users }
            single<BlogsDao> { Blogs }
            single<DatabaseProviderContract> { DatabaseProvider() }
        }
}
