package digital.thon.nc.application.model.response

import digital.thon.nc.data.model.Event
import kotlinx.serialization.Serializable

@Serializable
data class EventsResponse(
    override val status: State,
    override val message: String,
    val events: List<Event>,
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
            events: List<Event>,
        ) = EventsResponse(
            State.SUCCESS,
            message,
            events,
        )
    }
}
