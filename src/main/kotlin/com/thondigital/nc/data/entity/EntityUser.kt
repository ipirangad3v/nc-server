package com.thondigital.nc.data.entity

import com.thondigital.nc.data.database.table.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EntityUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntityUser>(Users)

    var email by Users.email
    var username by Users.username
    var password by Users.password
    var isAdmin = false
}