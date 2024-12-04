package net.aabergs.networkmanager.dal.account

import net.aabergs.networkmanager.dal.schema.TenantTable
import net.aabergs.networkmanager.dal.schema.UserTable
import net.aabergs.networkmanager.dal.schema.UserTenantTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Component
class AccountManagementDal {

    fun registerNewUser(userId: String, name: String, email: String): UserDto {

        val user = UserDto(userId, name, email)
        transaction {

            UserTable.insertReturning {
                it[UserTable.name] = name
                it[UserTable.userId] = userId
                it[UserTable.email] = email
            }.single().let {
                user.id = it[UserTable.id].value
                user.created = it[UserTable.created]
            }

            var tenant: TenantDto
            TenantTable.insertReturning {
                it[TenantTable.name] = name
                it[TenantTable.type] = "Personal"
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
            val userRow = UserTable.select(UserTable.id, UserTable.userId, UserTable.email, UserTable.name, UserTable.created)
                .where { UserTable.id eq id }
                .singleOrNull()

            userRow ?: return@transaction null

            val user: UserDto
            userRow.let {
                user = UserDto(it[UserTable.userId], it[UserTable.name], it[UserTable.email])
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

    fun getUserByUserId(userId: String) : UserDto? {
        val id = transaction {
            UserTable.select(UserTable.id)
                .where { UserTable.userId eq userId }
                .singleOrNull()?.get(UserTable.id)
        } ?: return null

        return getUserById(id.value)
    }
}