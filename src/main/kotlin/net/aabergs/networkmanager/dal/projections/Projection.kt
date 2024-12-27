package net.aabergs.networkmanager.dal.projections

import net.aabergs.networkmanager.bl.Event
import java.util.UUID

interface Projection {
    fun apply(event: Event, aggregateId: UUID)
}