package digital.thon.nc.application.model.response.auth

import digital.thon.nc.application.model.response.Response
import digital.thon.nc.application.model.response.State
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    override val status: State,
    override val message: String,
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
) : Response {
    companion object {
        fun failed(message: String) =
            AuthResponse(
                State.FAILED,
                message,
            )

        fun unauthorized(message: String) =
            AuthResponse(
                State.UNAUTHORIZED,
                message,
            )

        fun success(message: String) =
            AuthResponse(
                State.SUCCESS,
                message,
            )

        fun success(
            message: String,
            accessToken: String?,
            refreshToken: String?,
        ) = AuthResponse(
            State.SUCCESS,
            message,
            accessToken,
            refreshToken,
        )
    }
}