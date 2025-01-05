package net.aabergs.networkmanager.dal.projections

import kotlinx.datetime.Clock
import net.aabergs.networkmanager.bl.AggregateManager
import net.aabergs.networkmanager.bl.contact.ContactAggregate
import net.aabergs.networkmanager.dal.aggregate.AggregateDal
import net.aabergs.networkmanager.dal.contact.ContactListItemDto
import net.aabergs.networkmanager.dal.contact.ContactProjectionsDal
import net.aabergs.networkmanager.dal.dropAndCreateSchema
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.util.UUID

@SpringBootTest
@TestPropertySource("/test-application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactProjectionsDalTest {

    @Autowired
    private lateinit var database: Database

    @Autowired
    private lateinit var aggregateManager: AggregateManager

    @BeforeAll
    fun setUp() {
        dropAndCreateSchema(database)
    }

    @Test
    fun `when contact aggregate is saved and updated, the projection is updated successfully`() {

        /** First, let's create a new contact aggregate **/
        // Arrange
        val contactAggregate = ContactAggregate(UUID.randomUUID(), "Test Test", Clock.System.now(), 1)

        // Act
        aggregateManager.saveState(contactAggregate)
        val contacts = aggregateManager.getContactList(1)

        // Assert
        assertThat(contacts)
            .extracting("tenantId", "name")
            .containsOnly(tuple(1L, "Test Test"))

        /** Now, let's update the contact aggregate, and give it a new name **/
        // Arrange
        contactAggregate.rename("Test Test 2")

        // Act
        aggregateManager.saveState(contactAggregate)
        val updatedContacts = aggregateManager.getContactList(1)

        // Assert
        assertThat(updatedContacts)
            .extracting("tenantId", "name")
            .containsOnly(tuple(1L, "Test Test 2"))
    }

    @Test
    fun `when contact aggreate is deleted then projection is deleted`() {
        // Arrange
        val contactAggregate = ContactAggregate(UUID.randomUUID(), "Test Test", Clock.System.now(), 1)
        aggregateManager.saveState(contactAggregate)

        // Act
        contactAggregate.delete()
        aggregateManager.saveState(contactAggregate)
        val contacts = aggregateManager.getContactList(1)

        // Assert
        assertThat(contacts).isEmpty()
    }
}