package digital.thon.nc.application.auth.principal

import digital.thon.nc.data.model.User
import io.ktor.server.auth.Principal

class UserPrincipal(val user: User) : Principal
