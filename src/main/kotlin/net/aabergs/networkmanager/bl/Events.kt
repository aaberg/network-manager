package net.aabergs.networkmanager.bl

import kotlinx.datetime.Instant
import net.aabergs.networkmanager.bl.contact.Email
import net.aabergs.networkmanager.bl.contact.PhoneNumber
import java.util.UUID

sealed interface Event{}

data class NewContactCreated(val id: UUID, val name: String, val created: Instant, val tenantId: Long) : Event
data class EmailAdded(val email: Email)
data class EmailSetAsPrimary(val email: Email)
data class PhoneNumberAdded(val phoneNumber: PhoneNumber)
data class PhoneNumberSetAsPrimary(val phoneNumber: PhoneNumber)