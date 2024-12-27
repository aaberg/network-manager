package net.aabergs.networkmanager.dal.aggregate

import net.aabergs.networkmanager.bl.Event
import net.aabergs.networkmanager.dal.schema.EventsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.util.*

@Component
class AggregateDal {

    fun saveEvent(e: AggregateEventDto<Event>) {
        EventsTable.insert {
            it[aggregateId] = e.aggregateId
            it[version] = e.version
            it[aggregateType] = e.aggregateType
            it[eventData] = e.event
        }
    }

    fun getEventsByAggregateId(aggregateId: UUID) : List<AggregateEventDto<Event>> {
        return transaction {
            EventsTable.select(EventsTable.aggregateId, EventsTable.version, EventsTable.aggregateType, EventsTable.eventData)
                .where { EventsTable.aggregateId eq aggregateId }
                .map { rr ->
                    AggregateEventDto(
                        rr[EventsTable.aggregateId],
                        rr[EventsTable.version],
                        rr[EventsTable.aggregateType],
                        rr[EventsTable.eventData]
                    )
                }
        }

    }
}

data class AggregateEventDto<E: Event>(val aggregateId: UUID, val version: Int, val aggregateType: String, val event: E)