package net.aabergs.networkmanager.dal.contact

import net.aabergs.networkmanager.dal.schema.ContactListProjectionTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.util.*

@Component
class ContactProjectionsDal {

    fun getContactList(tenantId: Long) : List<ContactListItemDto> {
        return transaction {
            ContactListProjectionTable.select(ContactListProjectionTable.tenantId, ContactListProjectionTable.contactAggregateId, ContactListProjectionTable.name)
                .where { ContactListProjectionTable.tenantId eq tenantId }
                .map { rr ->
                    ContactListItemDto(
                        rr[ContactListProjectionTable.tenantId],
                        rr[ContactListProjectionTable.contactAggregateId],
                        rr[ContactListProjectionTable.name]
                    )
                }
        }

    }
}

data class ContactListItemDto(val tenantId: Long, val contactAggregateId: UUID, val name: String)