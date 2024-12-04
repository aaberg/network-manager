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

    fun registerAccount(principal: Principal, name: String, email: String) {
        val userId = principal.name
        accountManagementDal.registerNewUser(userId, name, email)
    }

}