package digital.thon.nc.application.model.request

import kotlinx.serialization.Serializable

@Serializable
data class IdpAuthenticationRequest(
    val username: String,
)
