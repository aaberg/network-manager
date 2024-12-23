package net.aabergs.networkmanager.bl

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.aabergs.networkmanager.bl.contact.Email
import net.aabergs.networkmanager.bl.contact.PhoneNumber
import java.util.UUID

@Serializable()
sealed interface Event{}

data class NewContactCreated(val id: UUID, val name: String, val created: Instant, val tenantId: Long) : Event
data class Renamed(val newName: String) : Event
data class EmailAdded(val email: Email) : Event
data class EmailRemoved(val email: Email) : Event
data class PrimaryEmailSet(val email: Email?) : Event
data class PhoneNumberAdded(val phoneNumber: PhoneNumber) : Event
data class PhoneNumberRemoved(val phoneNumber: PhoneNumber) : Event
data class PrimaryPhoneNumberSet(val phoneNumber: PhoneNumber?) : Event

fun <T:Event>serializeEvent(event: T) : String {
    return event.toString()
}

class EventSerializer(override val descriptor: SerialDescriptor) : KSerializer<Event> {
    override fun deserialize(decoder: Decoder): Event {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Event) {
        TODO("Not yet implemented")
    }

}