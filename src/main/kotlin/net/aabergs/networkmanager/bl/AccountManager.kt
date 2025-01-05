package net.aabergs.networkmanager.bl

import net.aabergs.networkmanager.dal.account.AccountManagementDal
import org.springframework.stereotype.Component
import java.security.Principal

@Component
class AccountManager(
    private val accountManagementDal: AccountManagementDal
) {

    fun getAccount(principal: Principal) : Account? {
        val userId = principal.name
        val userDto = accountManagementDal.getUserByUserId(userId)
            ?: return null

        return userDto.toAccount()
    }

    fun validateAccessAndGetTenant(principal: Principal, tenantId: Long) : Tenant {
        val userId = principal.name
        val userDto = accountManagementDal.getUserByUserId(userId)
            ?: throw IllegalStateException("User not found")

        return userDto.tenants.find { it.id == tenantId }?.toTenant()
            ?: throw UnknownTenantException("Tenant not found")
    }

    fun registerAccount(principal: Principal, name: String, email: String) : Account {
        val userId = principal.name
        val userDto = accountManagementDal.registerNewUser(userId, name, email)
        return userDto.toAccount()
    }

}