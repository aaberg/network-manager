package net.aabergs.networkmanager.bl

import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import net.aabergs.networkmanager.dal.account.AccountManagementDal
import net.aabergs.networkmanager.dal.account.TenantDto
import net.aabergs.networkmanager.dal.account.UserDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.Principal
import kotlin.test.assertNull

class AccountManagerTest {

    private lateinit var accountManagementDal:AccountManagementDal
    private lateinit var accountManager:AccountManager

    @BeforeEach
    fun setup() {
        accountManagementDal = mockk<AccountManagementDal>()
        accountManager = AccountManager(accountManagementDal)
    }

    @Test
    fun `when getAccount called with valid principal then account is returned`() {
        // Arrange
        val principal = Principal { "test_user_id" }
        val userDto = UserDto("test_user_id", "test", "test@email.com").apply {
            id = 1
            created = Clock.System.now()
            tenants = listOf(TenantDto("test", "Personal").apply {
                id = 1 })
        }

        every { accountManagementDal.getUserByUserId("test_user_id") } returns userDto
        // Act
        val account = accountManager.getAccount(principal)!!

        // Assert
        assert(account.email == "test@email.com")
        assert(account.name == "test")
        assert(account.tenants.size == 1)
        assert(account.tenants[0].name == "test")
        assert(account.tenants[0].type == TenantType.PERSONAL)
    }

    @Test
    fun `when getAccount called with valid principal but where no account exist then null`() {
        // Arrange
        val userId = "not_found_user"
        val princial = Principal{userId}

        every {accountManagementDal.getUserByUserId(userId)} returns null

        // Act
        val account = accountManager.getAccount(princial)

        // Assert
        assertNull(account)
    }
}