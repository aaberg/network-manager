package net.aabergs.networkmanager.bl

import net.aabergs.networkmanager.dal.account.UserDto

fun UserDto.toAccount() : Account {
    val account = Account(this.name, this.email, this.id!!, this.created!!)

    account.let {
        it.tenants = this.tenants.map { tenantDto ->
            val tenantType = when (tenantDto.type) {
                "Personal" -> TenantType.PERSONAL
                "Organization" -> TenantType.ORGANIZATION
                else -> throw IllegalArgumentException("Unknown tenant type ${tenantDto.type}")
            }
            return@map Tenant(tenantDto.id!!, tenantDto.name, tenantType)
        }
    }
    return account;

}