package net.aabergs.networkmanager.bl.contact

import kotlinx.serialization.Serializable
import net.aabergs.networkmanager.utils.UUIDSerializer
import java.util.UUID

@Serializable
data class PhoneNumber(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val number: String)
