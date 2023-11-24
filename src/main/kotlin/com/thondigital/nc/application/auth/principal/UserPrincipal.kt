package com.thondigital.nc.application.auth.principal

import com.thondigital.nc.data.model.User
import io.ktor.auth.Principal

class UserPrincipal(val user: User) : Principal
