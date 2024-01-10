package digital.thon.nc.application.model.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String,
)
