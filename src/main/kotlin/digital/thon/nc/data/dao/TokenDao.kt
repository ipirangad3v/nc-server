package digital.thon.nc.data.dao

import digital.thon.nc.data.model.Token

interface TokenDao {
    suspend fun store(
        userId: Int,
        token: String,
        expirationTime: String,
    ): String

    suspend fun getAllById(userId: Int): List<Token>

    suspend fun exists(
        userId: Int,
        token: String,
    ): Boolean

    suspend fun deleteById(tokenId: String): Boolean
}