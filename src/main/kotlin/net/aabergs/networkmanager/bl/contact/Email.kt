package net.aabergs.networkmanager.bl.contact

import kotlinx.serialization.Serializable
import net.aabergs.networkmanager.utils.UUIDSerializer
import java.util.*

@Serializable
data class Email(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val email: String
)
