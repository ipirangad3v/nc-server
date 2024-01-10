package digital.thon.nc.application.router

import digital.thon.nc.application.controller.event.EventController
import digital.thon.nc.application.model.request.event.CreateEventRequest
import digital.thon.nc.application.model.response.generateHttpResponse
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.eventApi() {
    val eventController by inject<EventController>()

    route("/events") {
        get("/list") {
            val allEvent = eventController.getAll()
            val response = generateHttpResponse(allEvent)
            call.respond(response.code, response.body)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get
            val event = eventController.getById(id)
            val response = generateHttpResponse(event)
            call.respond(response.code, response.body)
        }
        authenticate("jwt") {
            get("/update/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get
                val event = eventController.getById(id)
                val response = generateHttpResponse(event)
                call.respond(response.code, response.body)
            }
            get("/delete/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get
                val event = eventController.deleteById(id)
                val response = generateHttpResponse(event)
                call.respond(response.code, response.body)
            }
            get("create") {
                val createEventRequest = call.receive<CreateEventRequest>()
                val event =
                    eventController.store(
                        name = createEventRequest.name,
                        description = createEventRequest.description,
                        date = createEventRequest.date,
                        time = createEventRequest.time,
                        location = createEventRequest.location,
                        image = createEventRequest.image,
                        link = createEventRequest.link,
                    )
                val response = generateHttpResponse(event)
                call.respond(response.code, response.body)
            }
        }
    }
}
