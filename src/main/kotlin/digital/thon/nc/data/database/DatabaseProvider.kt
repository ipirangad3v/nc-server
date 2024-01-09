@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package digital.thon.nc.data.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import digital.thon.nc.data.database.table.Tokens
import digital.thon.nc.data.database.table.Users
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

@OptIn(DelicateCoroutinesApi::class)
class DatabaseProvider : DatabaseProviderContract, KoinComponent {
    private val dispatcher: CoroutineContext = newFixedThreadPoolContext(5, "database-pool")

    override fun init() {
        Database.connect(hikari())
        transaction {
            create(Users)
            create(Tokens)
        }
    }

    private fun hikari(): HikariDataSource {
        val apiKey = System.getenv("LOCAL_DEPLOYMENT")

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
            driverClassName = DRIVER_CLASS
            jdbcUrl = "jdbc:postgresql://$HOST:$PORT/$DB_NAME"
            username = USER
            password = DB_PASSWORD
            isAutoCommit = false
            maximumPoolSize = 5
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
            return HikariDataSource(this)
        }
    }

    companion object DatabaseConfig {
        const val DRIVER_CLASS = "org.postgresql.Driver"
        const val HOST = "localhost"
        const val PORT = 3500
        const val DB_NAME = "nc_server"
        const val USER = "postgres"
        const val DB_PASSWORD = "root"
    }

    // For heroku deployement
    private fun hikariHeroku(): HikariDataSource {
        val databaseHost = System.getenv("HOST_URL")
        val dbPort = System.getenv("DB_PORT")
        val dbName = System.getenv("DB_NAME")
        val dbPassword = System.getenv("DB_PASSWORD")
        val dbUser = System.getenv("DB_USER")

        HikariConfig().run {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://$databaseHost:$dbPort/$dbName"
            username = dbUser
            password = dbPassword
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
            return HikariDataSource(this)
        }
    }
}

interface DatabaseProviderContract {
    fun init()
}
