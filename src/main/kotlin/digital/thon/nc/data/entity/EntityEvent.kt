package digital.thon.nc.data.entity

import digital.thon.nc.data.database.table.Events
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EntityEvent(
    id: EntityID<Int>,
    var name: String,
    var description: String,
    var date: String,
    var time: String,
    var location: String,
    var image: String,
    var link: String,
) : IntEntity(id) {
    companion object : IntEntityClass<EntityEvent>(Events)
}
