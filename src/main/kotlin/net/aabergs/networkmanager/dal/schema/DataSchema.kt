package net.aabergs.networkmanager.dal.schema

import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
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

object ContactTable : LongIdTable("contact") {
    val tenantId = reference("tenant_id", TenantTable)
    val name = varchar("name", 255)
    val created = timestamp("created").default(Clock.System.now())
}

