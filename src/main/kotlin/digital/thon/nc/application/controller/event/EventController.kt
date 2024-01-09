package digital.thon.nc.application.controller.event

import digital.thon.nc.application.model.response.Response

class DefaultEventController : EventController {
    override suspend fun getAll(): Response {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: Int): Response {
        TODO("Not yet implemented")
    }

    override suspend fun store(
        name: String,
        description: String,
        date: String,
        time: String,
        location: String,
        image: String,
        link: String,
    ): Response {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: Int): Response {
        TODO("Not yet implemented")
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
        image: String,
        link: String,
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
