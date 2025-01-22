package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Clock
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
        contactAggregate.addEmail(email)
        contactAggregate.addPhoneNumber(PhoneNumber("12345678"))
        contactAggregate.primaryEmail = email
        contactAggregate.primaryPhoneNumber = PhoneNumber("12345678")
        contactAggregate.updateEmail(updatedEmail)
        contactAggregate.removeEmail(updatedEmail)
        contactAggregate.removePhoneNumber(PhoneNumber("12345678"))

        // Assert
        assertThat(contactAggregate.getUncommittedEvents()).hasSize(9)
        assertThat(contactAggregate.getUncommittedEvents()).containsExactly(
            NewContactCreated(id, "Kari Normann", createdTime, 1),
            ContactRenamed("Kari Normannsen"),
            EmailAdded(email),
            PhoneNumberAdded(PhoneNumber("12345678")),
            PrimaryEmailSet(email),
            PrimaryPhoneNumberSet(PhoneNumber("12345678")),
            EmailUpdated(updatedEmail),
            EmailRemoved(updatedEmail),
            PhoneNumberRemoved(PhoneNumber("12345678"))
        )
//        assert(contactAggregate.getUncommittedEvents()[0] == NewContactCreated(id, "Kari Normann", createdTime, 1))
//        assert(contactAggregate.getUncommittedEvents()[1] == ContactRenamed("Kari Normannsen"))
//        assert(contactAggregate.getUncommittedEvents()[2] == EmailAdded(email))
//        assert(contactAggregate.getUncommittedEvents()[3] == PhoneNumberAdded(PhoneNumber("12345678")))
//        assert(contactAggregate.getUncommittedEvents()[4] == PrimaryEmailSet(email))
//        assert(contactAggregate.getUncommittedEvents()[5] == PrimaryPhoneNumberSet(PhoneNumber("12345678")))
//
//        assert(contactAggregate.getUncommittedEvents()[6] == EmailUpdated(updatedEmail))
//        assert(contactAggregate.getUncommittedEvents()[7] == EmailRemoved(updatedEmail))
//        assert(contactAggregate.getUncommittedEvents()[8] == PhoneNumberRemoved(PhoneNumber("12345678")))

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

        // Act
        contactAggregate.addPhoneNumber(PhoneNumber("12345678"))

        // Assert
        assertThrows<InvalidStateException>("Phone number must be added to contact before it can be set as primary") {
            contactAggregate.primaryPhoneNumber = PhoneNumber("87654321")
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
}