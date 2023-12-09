package com.thondigital.nc.data.dao

import com.thondigital.nc.data.model.User

interface UserDao {
    suspend fun storeUser(
        email: String,
        username: String,
        password: String?,
        isAdmin: Boolean,
    ): User

    suspend fun findByID(userId: Int): User?

    suspend fun findByEmail(email: String): User?

    suspend fun isEmailAvailable(email: String): Boolean

    suspend fun updatePassword(
        userId: Int,
        password: String,
    )

    suspend fun deleteUser(userId: Int)
}