package com.thondigital.nc.data.model

import com.thondigital.nc.data.entity.EntityUser

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val password: String?,
    val isAdmin: Boolean,
) {
    companion object {
        fun fromEntity(entity: EntityUser) = User(entity.id.value, entity.email, entity.username, entity.password, entity.isAdmin)
    }
}
