package net.aabergs.networkmanager.dal.account

import net.aabergs.networkmanager.dal.dropAndCreateSchema
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("/test-application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountManagementDalTest(@Autowired val database: Database) {

    @BeforeAll
    fun setUp() {
        dropAndCreateSchema(database)
    }

    @Test
    fun `when registerNewUser is called with valid parameters, a user is successfully created with a private tenant`() {
        // Arrange
        val dal = AccountManagementDal()

        // Act
        val user = dal.registerNewUser("sometestid","test", "test@mail.com")

        val refetchedUser = dal.getUserById(user.id)

        // Assert
        assert(user.userId == "sometestid")
        assert(user.tenants.size == 1)

        assert(refetchedUser != null)
        assert(refetchedUser!!.id == user.id)
        assert(refetchedUser.userId == user.userId)
        assert(refetchedUser.name == user.name)
        assert(refetchedUser.created == user.created)
        assert(refetchedUser.tenants.size == 1)
        assert(refetchedUser.tenants[0].id == user.tenants[0].id)
    }

}