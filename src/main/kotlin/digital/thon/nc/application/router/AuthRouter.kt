package digital.thon.nc.application.router

import digital.thon.nc.application.controller.auth.AuthController
import digital.thon.nc.application.model.request.RefreshTokenRequest
import digital.thon.nc.application.model.request.ResetPasswordRequest
import digital.thon.nc.application.model.request.RevokeTokenRequest
import digital.thon.nc.application.model.request.SignInRequest
import digital.thon.nc.application.model.request.SignUpRequest
import digital.thon.nc.application.model.request.UpdatePasswordRequest
import digital.thon.nc.application.model.response.generateHttpResponse
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.authApi() {
    val authController by inject<AuthController>()

    route("/auth") {
        post("/signin") {
            val signInRequest = call.receive<SignInRequest>()
            val signInResponse = authController.signIn(signInRequest)
            val response = generateHttpResponse(signInResponse)
            call.respond(response.code, response.body)
        }

        post("/signup") {
            val signUpRequest = call.receive<SignUpRequest>()
            val signUpResponse = authController.signUp(signUpRequest)
            val response = generateHttpResponse(signUpResponse)
            call.respond(response.code, response.body)
        }

        route("/token") {
            post("/refresh") {
                val refreshTokenRequest = call.receive<RefreshTokenRequest>()
                val refreshTokenResponse =
                    authController.refreshToken(refreshTokenRequest)
                val response = generateHttpResponse(refreshTokenResponse)
                call.respond(response.code, response.body)
            }

            post("/revoke") {
                val revokeTokenRequest = call.receive<RevokeTokenRequest>()
                val revokeTokenResponse =
                    authController.revokeToken(revokeTokenRequest)
                val response = generateHttpResponse(revokeTokenResponse)
                call.respond(response.code, response.body)
            }
        }
        route("/delete") {
            authenticate("jwt") {
                post {
                    val deleteAccountResponse =
                        authController.deleteAccount(
                            this.context,
                        )
                    val response = generateHttpResponse(deleteAccountResponse)
                    call.respond(response.code, response.body)
                }
            }
        }

        route("/account") {
            authenticate("jwt") {
                get {
                    val accountResponse = authController.getAccountById(this.context)
                    val response = generateHttpResponse(accountResponse)
                    call.respond(response.code, response.body)
                }

                put("/password") {
                    val updatePasswordRequest = call.receive<UpdatePasswordRequest>()
                    val updatePasswordResponse =
                        authController.updateAccountPassword(
                            updatePasswordRequest,
                            this.context,
                        )
                    val response = generateHttpResponse(updatePasswordResponse)
                    call.respond(response.code, response.body)
                }
            }
        }

        // Not used with android client
        post("/reset-password") {
            val resetPasswordRequest = call.receive<ResetPasswordRequest>()
            val passwordResetResponse =
                authController.resetPassword(resetPasswordRequest.email)
            val response = generateHttpResponse(passwordResetResponse)
            call.respond(response.code, response.body)
        }

        // Not used with android client
        post("confirm-reset-password") {
            val tokenParameters = call.request.queryParameters
            val resetPasswordRequest = call.receive<UpdatePasswordRequest>()
            val resetPasswordResponse =
                authController.confirmPasswordReset(
                    tokenParameters,
                    resetPasswordRequest,
                )
            val response = generateHttpResponse(resetPasswordResponse)
            call.respond(response.code, response.body)
        }
    }
}
