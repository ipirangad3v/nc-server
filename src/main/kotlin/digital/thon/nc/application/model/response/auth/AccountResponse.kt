package digital.thon.nc.application.model.response.auth

import digital.thon.nc.application.model.response.Response
import digital.thon.nc.application.model.response.State
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    override val status: State,
    override val message: String,
    val id: Int? = null,
    val username: String? = null,
    val email: String? = null,
) : Response {
    companion object {

        fun failed(message: String) =
            AccountResponse(
                State.FAILED,
                message,
            )

        fun success(
            message: String,
            userId: Int,
            email: String,
            username: String,
        ) = AccountResponse(
            State.SUCCESS,
            message,
            userId,
            email,
            username,
        )
    }
}