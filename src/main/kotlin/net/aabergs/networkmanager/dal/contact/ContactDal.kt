package net.aabergs.networkmanager.dal.contact

import kotlinx.datetime.Instant
import net.aabergs.networkmanager.dal.schema.ContactTable
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Component
class ContactDal {

    fun createContact(tenantId: Long, name: String) : ContactDto {
        return transaction {
            ContactTable.insertReturning {
                it[ContactTable.tenantId] = tenantId
                it[ContactTable.name] = name
            }.single().let {
                return@transaction ContactDto(
                    it[ContactTable.id].value,
                    it[ContactTable.name],
                    it[ContactTable.created]
                )
            }
        }
    }

    fun getContactById(id: Long) : ContactDto? {
        return transaction {
            return@transaction ContactTable.select(ContactTable.id, ContactTable.name, ContactTable.created)
                .where { ContactTable.id eq id}
                .map { rr ->
                    ContactDto(rr[ContactTable.id].value, rr[ContactTable.name], rr[ContactTable.created]) }
                .firstOrNull()
        }
    }

    fun getAllTenantContacts(tenantId: Long) : List<ContactDto> {
        return transaction {
            return@transaction ContactTable.select(ContactTable.id, ContactTable.tenantId, ContactTable.name, ContactTable.created)
                .where { ContactTable.tenantId eq tenantId}
                .map { rr ->
                    ContactDto(rr[ContactTable.id].value, rr[ContactTable.name], rr[ContactTable.created]) }
        }
    }
}

data class ContactDto(val id: Long, val name: String, val created: Instant)