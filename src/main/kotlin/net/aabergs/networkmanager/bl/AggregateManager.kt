package net.aabergs.networkmanager.bl

import net.aabergs.networkmanager.bl.contact.ContactAggregate
import net.aabergs.networkmanager.dal.aggregate.AggregateDal
import net.aabergs.networkmanager.dal.aggregate.AggregateEventDto
import net.aabergs.networkmanager.dal.contact.ContactProjectionsDal
import net.aabergs.networkmanager.dal.projections.Projection
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.full.primaryConstructor

@Component
class AggregateManager(val aggregateDal: AggregateDal, val projections: List<Projection>) {

    fun saveState(aggregate: Aggregate) {
        transaction {
            aggregate.getUncommittedEvents().forEach { event ->
                aggregateDal.saveEvent(AggregateEventDto(aggregate.id, ++aggregate.version, aggregate.javaClass.typeName, event))
                projections.forEach { it.apply(event, aggregate.id) }
            }
        }
    }

    final inline fun <reified T: Aggregate> loadState(aggregateId: UUID, factory: () -> T) : T {
        val events = aggregateDal.getEventsByAggregateId(aggregateId)
        val aggregate = factory()
        aggregate.loadFromHistory(events.map { it.event })
        return aggregate
    }

    // Functions for fetching projections
    fun getContactList(tenantId: Long) = ContactProjectionsDal().getContactList(tenantId)
}