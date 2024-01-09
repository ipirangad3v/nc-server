package digital.thon.nc.data.di

import digital.thon.nc.data.dao.EventDao
import digital.thon.nc.data.dao.TokenDao
import digital.thon.nc.data.dao.UserDao
import digital.thon.nc.data.database.DatabaseProvider
import digital.thon.nc.data.database.DatabaseProviderContract
import digital.thon.nc.data.database.table.Events
import digital.thon.nc.data.database.table.Tokens
import digital.thon.nc.data.database.table.Users
import org.koin.dsl.module

object DaoModule {
    val koinBeans =
        module {
            single<TokenDao> { Tokens }
            single<UserDao> { Users }
            single<EventDao> { Events }
            single<DatabaseProviderContract> { DatabaseProvider() }
        }
}
