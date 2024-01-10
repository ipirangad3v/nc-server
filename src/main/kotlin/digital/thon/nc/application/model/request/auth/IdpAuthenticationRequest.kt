package digital.thon.nc.application.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class IdpAuthenticationRequest(
    val username: String,
)