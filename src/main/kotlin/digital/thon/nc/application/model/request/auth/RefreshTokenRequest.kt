package digital.thon.nc.application.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val token: String,
)