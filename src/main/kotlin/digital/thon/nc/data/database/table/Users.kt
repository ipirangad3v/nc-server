package digital.thon.nc.data.database.table

import digital.thon.nc.data.dao.UserDao
import digital.thon.nc.data.entity.EntityUser
import digital.thon.nc.data.model.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Users : IntIdTable(), UserDao {
    val email = varchar("email", length = 30).uniqueIndex()
    val username = varchar("username", length = 30)
    val password = text("password").nullable()
    val isAdmin = bool("isAdmin").default(false)

    override suspend fun storeUser(
        email: String,
        username: String,
        password: String?,
        isAdmin: Boolean,
    ): User =
        newSuspendedTransaction(Dispatchers.IO) {
            EntityUser.new {
                this.email = email
                this.username = username
                this.password = password
                this.isAdmin = isAdmin
            }.let {
                User.fromEntity(it)
            }
        }

    override suspend fun findByID(userId: Int): User? =
        newSuspendedTransaction(Dispatchers.IO) {
            EntityUser.findById(userId)
        }?.let {
            User.fromEntity(it)
        }

    override suspend fun findByEmail(email: String): User? =
        newSuspendedTransaction(Dispatchers.IO) {
            EntityUser.find {
                (Users.email eq email)
            }.firstOrNull()
        }?.let { User.fromEntity(it) }

    override suspend fun isEmailAvailable(email: String): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            EntityUser.find { Users.email eq email }.firstOrNull()
        } == null
    }

    override suspend fun updatePassword(
        userId: Int,
        password: String,
    ) {
        newSuspendedTransaction(Dispatchers.IO) {
            EntityUser[userId].apply {
                this.password = password
            }.id.value.toString()
        }
    }

    override suspend fun deleteUser(userId: Int) {
        newSuspendedTransaction(Dispatchers.IO) {
            EntityUser[userId].delete()
        }
    }
}
