package net.aabergs.networkmanager.dal.account

data class TenantDto(val name: String, val type: String) {
    var id: Long? = null
}
