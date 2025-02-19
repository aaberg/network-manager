package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import net.aabergs.networkmanager.bl.Icon
import net.aabergs.networkmanager.utils.UUIDSerializer
import java.util.*

@Serializable
data class LogEntry(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val timestamp: Instant,
    val type: String,
    val logNote: String,
    val icon: Icon
)