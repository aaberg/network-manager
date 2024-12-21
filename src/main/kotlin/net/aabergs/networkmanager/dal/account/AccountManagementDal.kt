package net.aabergs.networkmanager.dal.account

import net.aabergs.networkmanager.bl.TenantType
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
        return transaction {
            val user = createUserInternal(name, userId, email)

            val tenant = createTenantInternal(name, TenantType.PERSONAL)
            createUserTenantReferenceInternal(user.id, tenant.id)

            user.tenants = listOf(tenant)
            return@transaction user
        }
    }

    fun assignUserToTenant(user: UserDto, tenant: TenantDto) {

        transaction {
            UserTenantTable.insert {
                it[user_id] = user.id
                it[tenant_tenant] = tenant.id
            }
        }
    }

    fun getUserById(id: Long): UserDto? {

        return transaction {
            val userRow = UserTable.select(UserTable.id, UserTable.userId, UserTable.email, UserTable.name, UserTable.created)
                .where { UserTable.id eq id }
                .singleOrNull()

            userRow ?: return@transaction null

            val user = userRow.let {
                UserDto(it[UserTable.id].value, it[UserTable.userId], it[UserTable.name], it[UserTable.email], it[UserTable.created])
            }

            user.tenants = (TenantTable innerJoin UserTenantTable)
                .select(TenantTable.id, TenantTable.name, TenantTable.type)
                .where { UserTenantTable.user_id eq id }
                .map {
                    TenantDto(
                        it[TenantTable.id].value,
                        it[TenantTable.name],
                        it[TenantTable.type]
                    )
                }

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

    fun createTenant(name: String, type: TenantType) : TenantDto {
        return transaction {
            createTenantInternal(name, type)
        }
    }

    private fun createTenantInternal(name: String, type: TenantType) : TenantDto {
        return TenantTable.insertReturning {
            it[TenantTable.name] = name
            it[TenantTable.type] = type.toString()
        }.single().let {
            TenantDto(
                it[TenantTable.id].value,
                it[TenantTable.name],
                it[TenantTable.type]
            )
        }
    }

    private fun createUserInternal(name: String, userId: String, email: String) : UserDto {
        UserTable.insertReturning {
            it[UserTable.name] = name
            it[UserTable.userId] = userId
            it[UserTable.email] = email
        }.single().let {
            return UserDto(it[UserTable.id].value, userId, name, email, it[UserTable.created])
        }
    }

    private fun createUserTenantReferenceInternal(userId: Long, tenantId: Long) {
        UserTenantTable.insert {
            it[user_id] = userId
            it[tenant_tenant] = tenantId
        }
    }
}