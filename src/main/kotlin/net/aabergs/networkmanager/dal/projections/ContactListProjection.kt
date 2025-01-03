package net.aabergs.networkmanager.dal.projections

import net.aabergs.networkmanager.bl.ContactDeleted
import net.aabergs.networkmanager.bl.ContactRenamed
import net.aabergs.networkmanager.bl.Event
import net.aabergs.networkmanager.bl.NewContactCreated
import net.aabergs.networkmanager.dal.schema.ContactListProjectionTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Component
import java.util.*

@Component
class ContactListProjection : Projection {
    override fun apply(event: Event, aggregateId: UUID) {
        when (event) {
            is NewContactCreated -> {
                ContactListProjectionTable.insert {
                    it[tenantId] = event.tenantId
                    it[contactAggregateId] = event.id
                    it[name] = event.name
                }
            }

            is ContactRenamed -> {
                ContactListProjectionTable.update({ ContactListProjectionTable.contactAggregateId eq aggregateId}) {
                    it[name] = event.newName
                }
            }

            is ContactDeleted -> {
                ContactListProjectionTable.deleteWhere { ContactListProjectionTable.contactAggregateId eq aggregateId }
            }

            else -> {
                // Do nothing
            }
        }
    }
}