package digital.thon.nc.application.model.response.event

import digital.thon.nc.application.model.response.Response
import digital.thon.nc.application.model.response.State
import kotlinx.serialization.Serializable

@Serializable
data class EventsResponse(
    override val status: State,
    override val message: String,
    val events: List<EventResponse>,
) : Response {
    companion object {
        fun failed(message: String) =
            EventsResponse(
                State.FAILED,
                message,
                emptyList(),
            )

        fun success(
            message: String,
            events: List<EventResponse>,
        ) = EventsResponse(
            State.SUCCESS,
            message,
            events,
        )
    }
}