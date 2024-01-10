package digital.thon.nc.application.model.request.event

import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val id: Long,
)
