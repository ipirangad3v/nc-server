package digital.thon.nc.application.model.request.event

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(
    val name: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val link: String? = null,
    val image: String? = null,
)