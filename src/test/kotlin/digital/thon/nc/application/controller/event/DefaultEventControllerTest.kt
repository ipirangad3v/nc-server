package digital.thon.nc.application.controller.event

import digital.thon.nc.application.model.response.*
import digital.thon.nc.application.model.response.event.*
import digital.thon.nc.data.dao.*
import digital.thon.nc.data.model.*
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.*
import org.koin.dsl.*


class DefaultEventControllerTest {

    private val eventDao = mockk<EventDao>()
    private val eventController = DefaultEventController()

    @BeforeEach
    fun setup() {
        startKoin {
            modules(module {
                single { eventDao }
            })
        }
    }

    @AfterEach
    fun tearDown() {
        unloadKoinModules(module {
            single { mockk<EventDao>() }
        })
        stopKoin()
    }

    @Test
    fun `getAll should return success response when events are found`() = runBlocking {
        // Arrange
        val expectedEvents = listOf(
            Event(
                id = "1",
                name = "Event 1",
                description = "Description 1",
                date = "2021-10-10",
                time = "10:00",
                location = "Location 1",
                image = "Image 1",
                link = "Link 1",
            ),
            Event(
                id = "2",
                name = "Event 2",
                description = "Description 2",
                date = "2021-10-11",
                time = "11:00",
                location = "Location 2",
                image = "Image 2",
                link = "Link 2",
            ),
        )
        coEvery { eventDao.getAll() } returns expectedEvents

        // Act
        val response = eventController.getAll()

        // Assert
        assertEquals(
            EventsResponse.success(
                "Eventos recuperados com sucesso",
                expectedEvents.map { it.toEventResponse() }), response
        )
    }

    @Test
    fun `getAll should return failed response when an exception is thrown`() = runBlocking {
        // Arrange
        coEvery { eventDao.getAll() } throws Exception("Error fetching events")

        // Act
        val response = eventController.getAll()

        // Assert
        assertEquals(EventsResponse.failed("Falha ao recuperar eventos"), response)
    }

    @Test
    fun `getById should return success response when event is found`() = runBlocking {
        // Arrange
        val expectedEvent = Event(
            id = "1",
            name = "Event 1",
            description = "Description 1",
            date = "2021-10-10",
            time = "10:00",
            location = "Location 1",
            image = "Image 1",
            link = "Link 1",
        )
        coEvery { eventDao.getById(1) } returns expectedEvent

        // Act
        val response = eventController.getById(1)

        // Assert
        assertEquals(
            EventResponse.success(
                id = expectedEvent.id,
                name = expectedEvent.name,
                description = expectedEvent.description,
                date = expectedEvent.date,
                time = expectedEvent.time,
                location = expectedEvent.location,
                image = expectedEvent.image,
                link = expectedEvent.link,
                message = "Evento com id 1 recuperado com sucesso",
            ), response
        )
    }

    @Test
    fun `getById should return not found response when event is not found`() = runBlocking {
        // Arrange
        coEvery { eventDao.getById(1) } returns null

        // Act
        val response = eventController.getById(1)

        // Assert
        assertEquals(EventResponse.notFound("Evento com id 1 não encontrado"), response)
    }

    @Test
    fun `store should return success response when event is stored`() = runBlocking {
        // Arrange
        val eventId = 100
        coEvery { eventDao.store(any(), any(), any(), any(), any(), any(), any()) } returns eventId

        // Act
        val response =
            eventController.store("Event 1", "Description 1", "2021-10-10", "10:00", "Location 1", "Image 1", "Link 1")

        // Assert
        assertEquals(GeneralResponse.success("Successfully created event with id $eventId"), response)
    }

    @Test
    fun `store should return failed response when an exception is thrown`() = runBlocking {
        // Arrange
        coEvery {
            eventDao.store(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } throws Exception("Error storing event")

        // Act
        val response =
            eventController.store("Event 1", "Description 1", "2021-10-10", "10:00", "Location 1", "Image 1", "Link 1")

        // Assert
        assertEquals(EventsResponse.failed("Falha ao criar evento"), response)
    }

    @Test
    fun `update should return success response when event is updated`() = runBlocking {
        // Arrange
        coEvery { eventDao.update(any(), any(), any(), any(), any(), any(), any(), any()) } returns true

        // Act
        val response = eventController.update(
            1,
            "Event 1",
            "Description 1",
            "2021-10-10",
            "10:00",
            "Location 1",
            "Image 1",
            "Link 1"
        )

        // Assert
        assertEquals(GeneralResponse.success("Evento com id 1 atualizado com sucesso"), response)
    }

    @Test
    fun `update should return failed response when event is not updated`() = runBlocking {
        // Arrange
        coEvery { eventDao.update(any(), any(), any(), any(), any(), any(), any(), any()) } returns false

        // Act
        val response = eventController.update(
            1,
            "Event 1",
            "Description 1",
            "2021-10-10",
            "10:00",
            "Location 1",
            "Image 1",
            "Link 1"
        )

        // Assert
        assertEquals(EventsResponse.failed("Falha ao atualizar evento com id 1"), response)
    }

    @Test
    fun `deleteById should return success response when event is deleted`() = runBlocking {
        // Arrange
        coEvery { eventDao.deleteById(1) } returns true

        // Act
        val response = eventController.deleteById(1)

        // Assert
        assertEquals(GeneralResponse.success("Evento com id 1 deletado com sucesso"), response)
    }

    @Test
    fun `deleteById should return failed response when event is not deleted`() = runBlocking {
        // Arrange
        coEvery { eventDao.deleteById(1) } returns false

        // Act
        val response = eventController.deleteById(1)

        // Assert
        assertEquals(EventsResponse.failed("Falha ao deletar evento com id 1"), response)
    }
}
