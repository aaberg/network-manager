package net.aabergs.networkmanager.bl

import java.util.UUID

abstract class Aggregate(var version: Long = -1) {

    var id: UUID = DEFAULT_ID
        protected set

    private val uncommittedEvents = mutableListOf<Event>()
    fun getUncommittedEvents() = uncommittedEvents.toList()
    fun clearUncommittedEvents() = uncommittedEvents.clear()

    fun loadFromHistory(events: List<Event>) {
        for (event in events) {
            update(event)
            version += 1
        }
    }

    protected abstract fun update(event: Event)
    protected abstract fun ensureValidState();

    protected fun apply(event: Event) {
        update(event)
        ensureValidState()
        uncommittedEvents.add(event)
    }

    protected companion object {
        val DEFAULT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
}