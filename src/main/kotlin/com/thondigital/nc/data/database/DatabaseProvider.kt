@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package com.thondigital.nc.data.database

import com.thondigital.nc.data.database.table.Blogs
import com.thondigital.nc.data.database.table.Tokens
import com.thondigital.nc.data.database.table.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import java.net.URI
import kotlin.coroutines.CoroutineContext

@OptIn(DelicateCoroutinesApi::class)
class DatabaseProvider : DatabaseProviderContract, KoinComponent {
    private val dispatcher: CoroutineContext

    init {
        dispatcher = newFixedThreadPoolContext(5, "database-pool")
    }

    override fun init() {
        Database.connect(hikari())
        transaction {
            create(Users)
            create(Blogs)
            create(Tokens)
        }
    }

    private fun hikari(): HikariDataSource {
        val dotenv = dotenv()
        val apiKey = dotenv["LOCAL_DEPLOYMENT"]

        return when (apiKey) {
            "true" -> {
                hikariLocal()
            }

            "false" -> {
                hikariHeroku()
            }

            else -> {
                throw Exception("LOCAL_DEPLOYMENT must be set to true or false")
            }
        }
    }

    private fun hikariLocal(): HikariDataSource {
        HikariConfig().run {
            driverClassName = driverClass
            jdbcUrl = "jdbc:postgresql://${host}:${port}/${dbname}"
            username = user
            password = dbpassword
            isAutoCommit = false
            maximumPoolSize = 5
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
            return HikariDataSource(this)
        }
    }

    companion object DatabaseConfig {
        const val driverClass = "org.postgresql.Driver"
        const val host = "localhost"
        const val port = 3500
        const val dbname = "nc_server"
        const val user = "postgres"
        const val dbpassword = "root"
    }

    // For heroku deployement
    private fun hikariHeroku(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }


        val uri = URI(System.getenv("DATABASE_URL"))
        val username = uri.userInfo.split(":").toTypedArray()[0]
        val password = uri.userInfo.split(":").toTypedArray()[1]

        config.jdbcUrl =
            "jdbc:postgresql://" + uri.host + ":" + uri.port + uri.path + "?sslmode=require" + "&user=$username&password=$password"

        config.validate()

        return HikariDataSource(config)
    }
}

interface DatabaseProviderContract {
    fun init()
}