package digital.thon.nc.application.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val id: Long,
)
