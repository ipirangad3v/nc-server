package digital.thon.nc.data.model

import digital.thon.nc.application.model.response.EventResponse
import digital.thon.nc.application.model.response.State
import digital.thon.nc.data.entity.EntityEvent

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
    fun toEventResponse(): EventResponse {
        return EventResponse(
            id = id,
            name = name,
            description = description,
            date = date,
            time = time,
            location = location,
            image = image,
            link = link,
            message = "Successfully retrieved event with id $id",
            status = State.SUCCESS,
        )
    }

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