package digital.thon.nc.application.controller.event

import digital.thon.nc.application.controller.BaseController
import digital.thon.nc.application.model.response.GeneralResponse
import digital.thon.nc.application.model.response.Response
import digital.thon.nc.application.model.response.event.EventResponse
import digital.thon.nc.application.model.response.event.EventsResponse
import digital.thon.nc.data.dao.EventDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultEventController : BaseController(), EventController, KoinComponent {
    private val eventDao by inject<EventDao>()

    override suspend fun getAll(): Response {
        return try {
            val events = eventDao.getAll()
            EventsResponse.success(
                "Eventos recuperados com sucesso",
                events.map { it.toEventResponse() },
            )
        } catch (e: Exception) {
            EventsResponse.failed(
                "Falha ao recuperar eventos",
            )
        }
    }

    override suspend fun getById(id: Int): Response {
        return try {
            val event =
                eventDao.getById(id) ?: return EventResponse.notFound(
                    "Evento com id $id não encontrado",
                )
            EventResponse.success(
                id = event.id,
                name = event.name,
                description = event.description,
                date = event.date,
                time = event.time,
                location = event.location,
                image = event.image,
                link = event.link,
                message = "Evento com id $id recuperado com sucesso",
            )
        } catch (e: Exception) {
            EventsResponse.failed(
                "Falha ao recuperar evento com id $id",
            )
        }
    }

    override suspend fun store(
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String?,
        link: String?,
    ): Response {
        return try {
            val event =
                eventDao.store(
                    name = name,
                    description = description,
                    date = date,
                    time = time,
                    location = location,
                    image = image,
                    link = link,
                )
            GeneralResponse.success(
                "Successfully created event with id $event",
            )
        } catch (e: Exception) {
            EventsResponse.failed(
                "Falha ao criar evento",
            )
        }
    }

    override suspend fun update(
        id: Int,
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Response {
        return try {
            val success =
                eventDao.update(
                    id = id,
                    name = name,
                    description = description,
                    date = date,
                    time = time,
                    location = location,
                    image = image,
                    link = link,
                )
            when (success) {
                true ->
                    GeneralResponse.success(
                        "Evento com id $id atualizado com sucesso",
                    )

                false ->
                    EventsResponse.failed(
                        "Falha ao atualizar evento com id $id",
                    )
            }
        } catch (e: Exception) {
            EventsResponse.failed(
                "Falha ao atualizar evento com id $id",
            )
        }
    }

    override suspend fun deleteById(id: Int): Response {
        return try {
            val success = eventDao.deleteById(id)
            when (success) {
                true ->
                    GeneralResponse.success(
                        "Evento com id $id deletado com sucesso",
                    )

                false ->
                    EventsResponse.failed(
                        "Falha ao deletar evento com id $id",
                    )
            }
        } catch (e: Exception) {
            EventsResponse.failed(
                "Falha ao deletar evento com id $id",
            )
        }
    }
}

interface EventController {
    suspend fun getAll(): Response

    suspend fun getById(id: Int): Response

    suspend fun store(
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String? = null,
        link: String? = null,
    ): Response

    suspend fun update(
        id: Int,
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Response

    suspend fun deleteById(id: Int): Response
}