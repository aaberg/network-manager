package net.aabergs.networkmanager.bl

import kotlinx.datetime.Clock
import net.aabergs.networkmanager.bl.contact.ContactAggregate
import net.aabergs.networkmanager.bl.contact.Email
import net.aabergs.networkmanager.dal.dropAndCreateSchema
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregateManagerTest(
    @Autowired val aggregateManager: AggregateManager,
    @Autowired val database: Database
) {
    @BeforeAll
    fun setUp(){
        dropAndCreateSchema(database)
    }

    @Test
    fun `when the state of an aggregate is saved, the events are stored in the database`() {
        // Arrange
        val aggregate = ContactAggregate(UUID.randomUUID(), "Test Test", Clock.System.now(), 1)
        aggregate.rename("Test Test 2")
        val email = Email(UUID.randomUUID(),"something@else.com")
        aggregate.addEmail(email)

        // Act
        aggregateManager.saveState(aggregate)
        val refetchedAggregate:ContactAggregate = aggregateManager.loadState(aggregate.id){ContactAggregate()}

        // Assert
        assertEquals(aggregate.id, refetchedAggregate.id)
        assertEquals(aggregate.version, refetchedAggregate.version)
        assertEquals(aggregate.name, refetchedAggregate.name)
        assertEquals(aggregate.emails, refetchedAggregate.emails)
    }

    @Test
    fun `when the state of an aggregate is saved, the uncommitted events are cleared`() {
        // Arrange
        val aggregate = ContactAggregate(UUID.randomUUID(), "Test 123", Clock.System.now(), 1)
        aggregate.rename("Test 456")

        // Act
        aggregateManager.saveState(aggregate)

        // Assert
        assertThat(aggregate.getUncommittedEvents()).isEmpty()
    }
}