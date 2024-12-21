package net.aabergs.networkmanager.dal.contact

import net.aabergs.networkmanager.bl.TenantType
import net.aabergs.networkmanager.dal.account.AccountManagementDal
import net.aabergs.networkmanager.dal.schema.ContactTable
import net.aabergs.networkmanager.dal.schema.TenantTable
import net.aabergs.networkmanager.dal.schema.UserTable
import net.aabergs.networkmanager.dal.schema.UserTenantTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("/test-application.properties")
class ContactDalTest(@Autowired database: Database) {

    init {
        transaction(database) {
            SchemaUtils.drop(UserTable, TenantTable, UserTenantTable, ContactTable)
            SchemaUtils.create(UserTable, TenantTable, UserTenantTable, ContactTable)
        }
    }

    @Test
    fun `when createContact is called with valid parameters, a contact is successfully created`() {
        // Arrange
        val dal = ContactDal()
        val accountManagementDal = AccountManagementDal()

        val tenant = accountManagementDal.createTenant("test", TenantType.PERSONAL)

        // Act
        val contact = dal.createContact(tenant.id, "test")

        val refetchedContact = dal.getContactById(contact.id)

        // Assert
        assert(contact == refetchedContact)
    }

    @Test
    fun `when getContactById is called with an ID that does not exist, then null is returned`() {
        // Arrange
        val dal = ContactDal()

        // Act
        val contact = dal.getContactById(1)

        // Assert
        assert(contact == null)
    }

}