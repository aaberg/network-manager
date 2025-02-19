package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.aabergs.networkmanager.bl.*
import net.aabergs.networkmanager.bl.InvalidStateException
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals

class ContactAggregateTest {

    @Test
    fun `when ContactAggregate is updated expect correct uncommitted events`() {
        // Arrange
        val id = UUID.randomUUID()
        val createdTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "Kari Normann", createdTime, 1)

        // Act
        contactAggregate.rename("Kari Normannsen")
        val email = Email(UUID.randomUUID(), "kari@normannsen.com");
        val updatedEmail = Email(email.id, "aother@email.com")
        val phone = PhoneNumber(UUID.randomUUID(), "12345678")
        contactAggregate.addEmail(email)
        contactAggregate.addPhoneNumber(phone)
        contactAggregate.primaryEmail = email
        contactAggregate.primaryPhoneNumber = phone
        contactAggregate.updateEmail(updatedEmail)
        contactAggregate.removeEmail(updatedEmail)
        contactAggregate.removePhoneNumber(phone)

        // Assert
        assertThat(contactAggregate.getUncommittedEvents()).hasSize(9)
        assertThat(contactAggregate.getUncommittedEvents()).containsExactly(
            NewContactCreated(id, "Kari Normann", createdTime, 1),
            ContactRenamed("Kari Normannsen"),
            EmailAdded(email),
            PhoneNumberAdded(phone),
            PrimaryEmailSet(email),
            PrimaryPhoneNumberSet(phone),
            EmailUpdated(updatedEmail),
            EmailRemoved(updatedEmail),
            PhoneNumberRemoved(phone)
        )

        assertThat(contactAggregate.name).isEqualTo("Kari Normannsen")
        assertThat(contactAggregate.emails).isEmpty()
        assertThat(contactAggregate.phoneNumbers).isEmpty()
        assertThat(contactAggregate.primaryEmail).isNull()
        assertThat(contactAggregate.primaryPhoneNumber).isNull()
        assertThat(contactAggregate.tenantId).isEqualTo(1)
    }

    @Test
    fun `when ContactAggregate is updated with invalid primary email expect InvalidStateException`() {
        // Arrange
        val id = UUID.randomUUID()
        val createdTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "Kari Normann", createdTime, 1)

        // Act
        contactAggregate.addEmail(Email(UUID.randomUUID(), "kari@normann.no"))

        // Assert
        assertThrows<InvalidStateException>("Email must be added to contact before it can be set as primary") {
            contactAggregate.primaryEmail = Email(UUID.randomUUID(), "something@wrong.com")
        }
    }

    @Test
    fun `when ContactAggregate is updated with invalid primary phone number expect InvalidStateException`() {
        // Arrange
        val id = UUID.randomUUID()
        val createdTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "Kari Normann", createdTime, 1)
        val phoneId = UUID.randomUUID()

        // Act
        contactAggregate.addPhoneNumber(PhoneNumber(phoneId, "12345678"))

        // Assert
        assertThrows<InvalidStateException>("Phone number must be added to contact before it can be set as primary") {
            contactAggregate.primaryPhoneNumber = PhoneNumber(phoneId, "87654321")
        }
    }

    @Test
    fun `when ContactAggregate removeEmail is called with an email that does not exist expect InvalidStateException`() {
        // Arrange
        val id = UUID.randomUUID()
        val createdTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "Kari Normann", createdTime, 1)
        contactAggregate.addEmail(Email(UUID.randomUUID(), "kari@normann.com"))

        // Act
        assertThrows<InvalidStateException>("Email not found in contact") {
            contactAggregate.removeEmail(Email(UUID.randomUUID(), "something@else.com"))
        }

        // Assert
        assertEquals(1, contactAggregate.emails.size)
        assertEquals("kari@normann.com", contactAggregate.emails[0].email)
    }

    @Test
    fun `when ContactAggregate updateNote is called note is successfully updated`() {
        // Arrange
        val id = UUID.randomUUID()
        val createTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "The Dude", createTime, 1)

        // Assert initial state
        assertThat(contactAggregate.note).isEqualTo("")
        assertThat(contactAggregate.getUncommittedEvents()).containsExactly(
            NewContactCreated(id, "The Dude", createTime, 1)
        )

        // Act
        contactAggregate.updateNote("This is a very interesting note")

        // Assert
        assertThat(contactAggregate.note).isEqualTo("This is a very interesting note")
        assertThat(contactAggregate.getUncommittedEvents()).containsExactly(
            NewContactCreated(id, "The Dude", createTime, 1),
            NoteUpdated("This is a very interesting note")
        )
    }

    @Test
    fun `when logEvent and removeLoggedEvent are called expect correct events`() {
        // Arrange
        val id = UUID.randomUUID()
        val createTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "The Dude", createTime, 1)
        val logEntry = LogEntry(UUID.randomUUID(), Clock.System.now(), "custom type", "This is a log entry", Icon.Other)

        // Assert that logEvent works
        contactAggregate.logEvent(logEntry)
        assertThat(contactAggregate.connectionLog).containsExactly(logEntry)

        // Assert that the logEvent can be removed
        contactAggregate.removeLoggedEvent(logEntry)
        assertThat(contactAggregate.connectionLog).isEmpty()

        // Assert events
        assertThat(contactAggregate.getUncommittedEvents()).containsExactly(
            NewContactCreated(id, "The Dude", createTime, 1),
            ConnectionLogEntryAdded(logEntry),
            ConnectionLogEntryRemoved(logEntry)
        )
    }
}