package digital.thon.nc.data.database.table

import digital.thon.nc.data.dao.TokenDao
import digital.thon.nc.data.entity.EntityToken
import digital.thon.nc.data.entity.EntityUser
import digital.thon.nc.data.model.Token
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

object Tokens : UUIDTable(), TokenDao {
    val user = reference("user", Users)
    val token = varchar("token", 512)
    val expirationTime = varchar("expiration_time", 128)

    override suspend fun store(
        userId: Int,
        token: String,
        expirationTime: String,
    ): String =
        newSuspendedTransaction(Dispatchers.IO) {
            EntityToken.new {
                this.user = EntityUser[userId]
                this.token = token
                this.expirationTime = expirationTime
            }.id.value.toString()
        }

    override suspend fun getAllById(userId: Int): List<Token> =
        newSuspendedTransaction(Dispatchers.IO) {
            EntityToken.find { user eq userId }
                .sortedByDescending { it.id }
                .map { Token.fromEntity(it) }
        }

    override suspend fun exists(
        userId: Int,
        token: String,
    ): Boolean =
        newSuspendedTransaction(Dispatchers.IO) {
            EntityToken.find {
                ((Tokens.token eq token) and (user eq userId))
            }.firstOrNull() != null
        }

    override suspend fun deleteById(tokenId: String): Boolean =
        newSuspendedTransaction(Dispatchers.IO) {
            val token = EntityToken.findById(UUID.fromString(tokenId))
            token?.run {
                delete()
                true
            }
            false
        }
}