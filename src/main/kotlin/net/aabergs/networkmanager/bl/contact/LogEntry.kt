package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import net.aabergs.networkmanager.utils.UUIDSerializer
import java.util.*

@Serializable
data class LogEntry(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val entryTime: Instant,
    val type: LogEntryType,
    val logNote: String)

enum class LogEntryType {
    Email, Phone, Message, Other
}