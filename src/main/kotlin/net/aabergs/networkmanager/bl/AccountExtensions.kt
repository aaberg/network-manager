package net.aabergs.networkmanager.bl

import net.aabergs.networkmanager.dal.account.TenantDto
import net.aabergs.networkmanager.dal.account.UserDto

fun UserDto.toAccount() : Account {
    val account = Account(this.name, this.email, this.id, this.created)

    account.let {
        it.tenants = this.tenants.map { tenantDto ->
            val tenantType = when (tenantDto.type) {
                "PERSONAL" -> TenantType.PERSONAL
                "ORGANIZATION" -> TenantType.ORGANIZATION
                else -> throw IllegalArgumentException("Unknown tenant type ${tenantDto.type}")
            }
            return@map Tenant(tenantDto.id, tenantDto.name, tenantType)
        }
    }
    return account;
}

fun TenantDto.toTenant() : Tenant {
    val tenantType = when (this.type) {
        "PERSONAL" -> TenantType.PERSONAL
        "ORGANIZATION" -> TenantType.ORGANIZATION
        else -> throw IllegalArgumentException("Unknown tenant type ${this.type}")
    }
    return Tenant(this.id, this.name, tenantType)
}