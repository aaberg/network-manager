package net.aabergs.networkmanager.dal.account

import kotlinx.datetime.Instant

data class UserDto(val id: Long, val userId: String, val name: String, val email: String, val created: Instant) {
    var tenants: List<TenantDto> = emptyList()
}
