package com.thondigital.nc.data.entity

import com.thondigital.nc.data.database.table.Tokens
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class EntityToken(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntityToken>(Tokens)

    var user by EntityUser referencedOn Tokens.user
    var token by Tokens.token
    var expirationTime by Tokens.expirationTime
}
