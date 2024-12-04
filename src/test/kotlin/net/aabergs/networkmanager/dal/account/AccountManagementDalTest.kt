package net.aabergs.networkmanager.dal.account

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
class AccountManagementDalTest(@Autowired database: Database) {

    init {
        transaction(database) {
            SchemaUtils.drop(UserTable, TenantTable, UserTenantTable)
            SchemaUtils.create(UserTable, TenantTable, UserTenantTable)
        }
    }

    @Test
    fun `when registerNewUser is called with valid parameters, a user is successfully created with a private tenant`() {
        // Arrange
        val dal = AccountManagementDal()

        // Act
        val user = dal.registerNewUser("sometestid","test", "test@mail.com")

        val refetchedUser = dal.getUserById(user.id!!)

        // Assert
        assert(user.id != null)
        assert(user.userId == "sometestid")
        assert(user.created != null)
        assert(user.tenants.size == 1)
        assert(user.tenants[0].id != null)

        assert(refetchedUser != null)
        assert(refetchedUser!!.id == user.id)
        assert(refetchedUser.userId == user.userId)
        assert(refetchedUser.name == user.name)
        assert(refetchedUser.created == user.created)
        assert(refetchedUser.tenants.size == 1)
        assert(refetchedUser.tenants[0].id == user.tenants[0].id)
    }

}