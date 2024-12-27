package net.aabergs.networkmanager.dal.schema

import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import net.aabergs.networkmanager.bl.Event
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TenantTable : LongIdTable("tenant") {
    val name = varchar("name", 255)
    val type = varchar("type", 15)
}

object UserTable : LongIdTable("user") {
    val userId = varchar("user_id", 255).uniqueIndex()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val created = timestamp("created").default(Clock.System.now())
}

object UserTenantTable : Table("user_tenant") {
    val user_id = reference("user", UserTable)
    val tenant_tenant = reference("tenant", TenantTable)

    override val primaryKey = PrimaryKey(user_id, tenant_tenant, name = "PK_UserTenant_User_Tenant")
}

// Remember to remove
object ContactTable : LongIdTable("contact") {
    val tenantId = reference("tenant_id", TenantTable)
    val name = varchar("name", 255)
    val created = timestamp("created").default(Clock.System.now())
}

object EventsTable : Table("events") {
    val aggregateId = uuid("aggregate_id")
    val version = integer("version")
    val aggregateType = text("aggregate_type")
    val eventData = jsonb<Event>("event_data", Json.Default)

    override val primaryKey = PrimaryKey(aggregateId, version, name = "PK_Events")
}

// Projections
val projectionsSchema = Schema("projections")

object ContactListProjectionTable : LongIdTable("projections.contact_list") {
    val tenantId = long("tenant_id")
    val contactAggregateId = uuid("contact_aggregate_id")

    val name = text("name")
}

