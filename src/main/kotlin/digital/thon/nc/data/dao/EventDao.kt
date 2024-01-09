package digital.thon.nc.data.dao

import digital.thon.nc.data.model.Event

interface EventDao {
    suspend fun getAll(): List<Event>

    suspend fun getById(id: Int): Event?

    suspend fun store(
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Int

    suspend fun update(
        id: Int,
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Boolean

    suspend fun deleteById(id: Int): Boolean
}
