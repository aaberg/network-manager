package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Clock
import net.aabergs.networkmanager.bl.*
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
        contactAggregate.addEmail(Email("kari@normannsen.com"))
        contactAggregate.addPhoneNumber(PhoneNumber("12345678"))
        contactAggregate.primaryEmail = Email("kari@normannsen.com")
        contactAggregate.primaryPhoneNumber = PhoneNumber("12345678")
        contactAggregate.removeEmail(Email("kari@normannsen.com"))
        contactAggregate.removePhoneNumber(PhoneNumber("12345678"))

        // Assert
        assert(contactAggregate.getUncommittedEvents().size == 8)
        assert(contactAggregate.getUncommittedEvents()[0] == NewContactCreated(id, "Kari Normann", createdTime, 1))
        assert(contactAggregate.getUncommittedEvents()[1] == ContactRenamed("Kari Normannsen"))
        assert(contactAggregate.getUncommittedEvents()[2] == EmailAdded(Email("kari@normannsen.com")))
        assert(contactAggregate.getUncommittedEvents()[3] == PhoneNumberAdded(PhoneNumber("12345678")))
        assert(contactAggregate.getUncommittedEvents()[4] == PrimaryEmailSet(Email("kari@normannsen.com")))
        assert(contactAggregate.getUncommittedEvents()[5] == PrimaryPhoneNumberSet(PhoneNumber("12345678")))
        assert(contactAggregate.getUncommittedEvents()[6] == EmailRemoved(Email("kari@normannsen.com")))
        assert(contactAggregate.getUncommittedEvents()[7] == PhoneNumberRemoved(PhoneNumber("12345678")))

        assert(contactAggregate.name == "Kari Normannsen")
        assert(contactAggregate.emails.isEmpty())
        assert(contactAggregate.phoneNumbers.isEmpty())
        assert(contactAggregate.primaryEmail == null)
        assert(contactAggregate.primaryPhoneNumber == null)
        assertEquals(1, contactAggregate.tenantId)
    }

    @Test
    fun `when ContactAggregate is updated with invalid primary email expect InvalidStateException`() {
        // Arrange
        val id = UUID.randomUUID()
        val createdTime = Clock.System.now()
        val contactAggregate = ContactAggregate(id, "Kari Normann", createdTime, 1)

        // Act
        contactAggregate.addEmail(Email("kari@normann.no"))

        // Assert
        assertThrows<InvalidStateException>("Email must be added to contact before it can be set as primary") {
            contactAggregate.primaryEmail = Email("something@wrong.com")
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
        contactAggregate.addEmail(Email("kari@normann.com"))

        // Act
        assertThrows<InvalidStateException>("Email not found in contact") {
            contactAggregate.removeEmail(Email("something@else.com"))
        }

        // Assert
        assertEquals(1, contactAggregate.emails.size)
        assertEquals(Email("kari@normann.com"), contactAggregate.emails[0])
    }
}