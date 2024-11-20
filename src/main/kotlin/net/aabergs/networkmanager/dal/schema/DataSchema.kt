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
    val name = varchar("name", 255)
    val created = timestamp("created").default(Clock.System.now())
}

object UserTenantTable : Table("user_tenant") {
    val user = reference("user", UserTable)
    val tenant = reference("tenant", TenantTable)

    override val primaryKey = PrimaryKey(user, tenant, name = "PK_UserTenant_User_Tenant")
}

