package net.aabergs.networkmanager.bl

import net.aabergs.networkmanager.bl.contact.ContactAggregate
import net.aabergs.networkmanager.dal.aggregate.AggregateDal
import net.aabergs.networkmanager.dal.aggregate.AggregateEventDto
import net.aabergs.networkmanager.dal.projections.Projection
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.util.*

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

    fun loadState(aggregateId: UUID) : Aggregate {
        val events = aggregateDal.getEventsByAggregateId(aggregateId)
        val aggregate = createAggregate(events.first().aggregateType)
        aggregate.loadFromHistory(events.map { it.event })

        return aggregate
    }

    private fun createAggregate(aggregateType: String) : Aggregate {

        return when (aggregateType) {
            ContactAggregate::class.java.typeName -> ContactAggregate()
            else -> throw IllegalArgumentException("Unknown aggregate type: $aggregateType")
        }
    }
}