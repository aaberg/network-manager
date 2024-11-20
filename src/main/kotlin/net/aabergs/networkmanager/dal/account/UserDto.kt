package net.aabergs.networkmanager.dal.account

import kotlinx.datetime.Instant

data class UserDto(val name: String) {
    var id: Long? = null
    var created: Instant? = null
    var tenants: List<TenantDto> = emptyList()
}
