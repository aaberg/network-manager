package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.aabergs.networkmanager.bl.Aggregate
import net.aabergs.networkmanager.bl.Event
import net.aabergs.networkmanager.bl.NewContactCreated
import java.util.*
import kotlin.reflect.typeOf

class ContactAggregate() : Aggregate() {
    constructor(id: UUID, name: String, created: Instant, tenantId: Long) : this() {
        apply(NewContactCreated(id, name, created, tenantId))
    }

    var name = ""
        private set

    var created = Instant.DISTANT_PAST

    var tenantId = 0L

    private var _emails = mutableListOf<Email>()
    var emails = _emails.asIterable()
        private set

    private var _phoneNumbers = mutableListOf<PhoneNumber>()
    var phoneNumbers = _phoneNumbers.asIterable()
        private set

    override fun update(event: Event) {
        when (event) {
            is NewContactCreated -> {
                id = event.id
                name = event.name
                created = event.created
                tenantId = event.tenantId
            }
        }
    }

    override fun ensureValidState() {
        TODO("Not yet implemented")
    }
}