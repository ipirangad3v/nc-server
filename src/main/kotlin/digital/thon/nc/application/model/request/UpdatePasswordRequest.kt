package digital.thon.nc.application.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
)
