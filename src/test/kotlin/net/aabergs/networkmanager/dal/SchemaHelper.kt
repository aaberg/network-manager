package net.aabergs.networkmanager.dal

import net.aabergs.networkmanager.dal.schema.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun dropAndCreateSchema(database: Database) {
    transaction(database) {
        SchemaUtils.drop(UserTable, TenantTable, UserTenantTable, ContactTable, EventsTable)
        SchemaUtils.dropSchema(projectionsSchema, cascade = true)

        SchemaUtils.create(UserTable, TenantTable, UserTenantTable, ContactTable, EventsTable)
        SchemaUtils.createSchema(projectionsSchema)
        SchemaUtils.create(ContactListProjectionTable)
    }
}