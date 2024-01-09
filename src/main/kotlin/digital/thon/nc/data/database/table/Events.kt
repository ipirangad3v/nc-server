package digital.thon.nc.data.database.table

import digital.thon.nc.data.dao.EventDao
import digital.thon.nc.data.entity.EntityEvent
import digital.thon.nc.data.model.Event
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Events : IntIdTable(), EventDao {
    override suspend fun getAll(): List<Event> {
        return newSuspendedTransaction(Dispatchers.IO) {
            EntityEvent.all().map {
                Event.fromEntity(it)
            }
        }
    }

    override suspend fun getById(id: Int): Event? {
        return newSuspendedTransaction(Dispatchers.IO) {
            EntityEvent.findById(id)?.let {
                Event.fromEntity(it)
            }
        }
    }

    override suspend fun store(
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Int {
        return newSuspendedTransaction(Dispatchers.IO) {
            EntityEvent.new {
                this.name = name
                this.description = description
                this.date = date
                this.time = time
                this.location = location
                this.image = image
                this.link = link
            }.id.value
        }
    }

    override suspend fun update(
        id: Int,
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            EntityEvent.findById(id)?.let {
                it.name = name
                it.description = description
                it.date = date
                it.time = time
                it.location = location
                it.image = image
                it.link = link
                true
            } ?: false
        }
    }

    override suspend fun deleteById(id: Int): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            try {
                EntityEvent.findById(id)?.delete()
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
