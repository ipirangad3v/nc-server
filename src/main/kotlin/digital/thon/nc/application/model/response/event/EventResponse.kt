package digital.thon.nc.application.model.response.event

import digital.thon.nc.application.model.response.Response
import digital.thon.nc.application.model.response.State
import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    override val status: State,
    override val message: String,
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null,
    val time: String? = null,
    val location: String? = null,
    val image: String? = null,
    val link: String? = null,
) : Response {
    companion object {
        fun failed(message: String) =
            EventResponse(
                State.FAILED,
                message,
            )

        fun notFound(message: String) =
            EventResponse(
                State.NOT_FOUND,
                message,
            )

        fun success(
            message: String,
            id: String,
            name: String,
            description: String,
            date: String,
            time: String,
            location: String,
            image: String?,
            link: String?,
        ) = EventResponse(
            State.SUCCESS,
            message,
            id,
            name,
            description,
            date,
            time,
            location,
            image, link,
        )
    }
}