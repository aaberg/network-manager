package net.aabergs.networkmanager.bl

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import net.aabergs.networkmanager.bl.contact.Email
import net.aabergs.networkmanager.bl.contact.PhoneNumber
import net.aabergs.networkmanager.utils.UUIDSerializer
import java.util.*

@Serializable
sealed interface Event{}

@Serializable data class NewContactCreated(@Serializable(with = UUIDSerializer::class) val id: UUID, val name: String, val created: Instant, val tenantId: Long) : Event
@Serializable data class ContactRenamed(val newName: String) : Event
@Serializable data class EmailAdded(val email: Email) : Event
@Serializable data class EmailRemoved(val email: Email) : Event
@Serializable data class PrimaryEmailSet(val email: Email?) : Event
@Serializable data class PhoneNumberAdded(val phoneNumber: PhoneNumber) : Event
@Serializable data class PhoneNumberRemoved(val phoneNumber: PhoneNumber) : Event
@Serializable data class PrimaryPhoneNumberSet(val phoneNumber: PhoneNumber?) : Event
@Serializable class ContactDeleted : Event

//object UUIDSerializer : KSerializer<UUID> {
//    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
//
//    override fun deserialize(decoder: Decoder): UUID {
//        return UUID.fromString(decoder.decodeString())
//    }
//
//    override fun serialize(encoder: Encoder, value: UUID) {
//        encoder.encodeString(value.toString())
//    }
