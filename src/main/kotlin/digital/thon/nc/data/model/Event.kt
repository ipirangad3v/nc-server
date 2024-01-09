package digital.thon.nc.data.model

import digital.thon.nc.data.entity.EntityEvent
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val name: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val image: String? = null,
    val link: String? = null,
) {
    companion object {
        fun fromEntity(entity: EntityEvent): Event {
            return Event(
                id = entity.id.value.toString(),
                name = entity.name,
                description = entity.description,
                date = entity.date,
                time = entity.time,
                location = entity.location,
                image = entity.image,
                link = entity.link,
            )
        }
    }
}
