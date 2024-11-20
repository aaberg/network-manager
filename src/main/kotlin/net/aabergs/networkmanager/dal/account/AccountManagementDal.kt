package net.aabergs.networkmanager.dal.account

import net.aabergs.networkmanager.dal.schema.TenantTable
import net.aabergs.networkmanager.dal.schema.UserTable
import net.aabergs.networkmanager.dal.schema.UserTenantTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.transaction

class AccountManagementDal {

    fun registerNewUser(name: String): UserDto {

        val user = UserDto(name)
        transaction {

            UserTable.insertReturning {
                it[UserTable.name] = name
            }.single().let {
                user.id = it[UserTable.id].value
                user.created = it[UserTable.created]
            }

            var tenant: TenantDto
            TenantTable.insertReturning {
                it[TenantTable.name] = name
                it[TenantTable.type] = "Private"
            }.single().let {
                tenant = TenantDto(
                    it[TenantTable.name],
                    it[TenantTable.type]
                )
                tenant.id = it[TenantTable.id].value
            }

            UserTenantTable.insert {
                it[UserTenantTable.user] = user.id!!
                it[UserTenantTable.tenant] = tenant.id!!
            }

            user.tenants = listOf(tenant)

        }
        return user;
    }

    fun assignUserToTenant(user: UserDto, tenant: TenantDto) {

        transaction {
            UserTenantTable.insert {
                it[UserTenantTable.user] = user.id!!
                it[UserTenantTable.tenant] = tenant.id!!
            }
        }
    }

    fun getUserById(id: Long): UserDto? {

        return transaction {
            val userRow = UserTable.select(UserTable.id, UserTable.name, UserTable.created)
                .where { UserTable.id eq id }
                .singleOrNull()

            userRow ?: return@transaction null

            val user: UserDto
            userRow.let {
                user = UserDto(it[UserTable.name])
                user.id = it[UserTable.id].value
                user.created = it[UserTable.created]
            }

            val tenants = mutableListOf<TenantDto>()

            UserTenantTable.select(UserTenantTable.tenant)
                .where { UserTenantTable.user eq id }
                .forEach {
                    TenantTable.select(TenantTable.id, TenantTable.name, TenantTable.type)
                        .where { TenantTable.id eq it[UserTenantTable.tenant] }
                        .singleOrNull()?.let {
                            val tenant = TenantDto(
                                it[TenantTable.name],
                                it[TenantTable.type],
                            )
                            tenant.id = it[TenantTable.id].value
                            tenants += tenant
                        }
                }
            user.tenants = tenants

            return@transaction user
        }
    }
}