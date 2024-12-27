package net.aabergs.networkmanager.dal.aggregate

import kotlinx.datetime.Clock
import net.aabergs.networkmanager.bl.Event
import net.aabergs.networkmanager.bl.NewContactCreated
import net.aabergs.networkmanager.dal.dropAndCreateSchema
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource("/test-application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregateDalTest {

    @Autowired
    private lateinit var database: Database

    @BeforeAll
    fun setUp() {
        dropAndCreateSchema(database)
    }

    @Test
    fun `when saveEvent is called with valid parameters, an event is successfully saved`() {
        // Arrange
        val dal = AggregateDal()
        val id = UUID.randomUUID()
        val origEvent = NewContactCreated(UUID.randomUUID(), "Test Test", Clock.System.now(), 1)

        var events: List<AggregateEventDto<Event>> = emptyList()
        // Act
        transaction {
            dal.saveEvent(AggregateEventDto(id, 0, "sometype", origEvent))
            events = dal.getEventsByAggregateId(id)
        }

        // Assert
        assertEquals(1, events.size)
        assertEquals(events[0].aggregateId, id)
        assertEquals(events[0].aggregateType, "sometype")
        assertEquals(events[0].event, origEvent)
    }
}