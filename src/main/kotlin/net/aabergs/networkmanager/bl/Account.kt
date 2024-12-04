package net.aabergs.networkmanager.bl

import kotlinx.datetime.Instant

data class Account(val name: String, val email: String, val id: Long, val created: Instant) {
    var tenants: List<Tenant> = listOf()

    companion object {
        val NOT_FOUND = Account("NOTFOUND", "NOTFOUND", -1, Instant.DISTANT_PAST)
    }
}

data class Tenant(val id: Long, val name: String, val type: TenantType)

enum class TenantType {
    PERSONAL,
    ORGANIZATION
}