package com.thondigital.nc.application.router

import com.thondigital.nc.application.controller.auth.AuthController
import com.thondigital.nc.application.model.request.IdpAuthenticationRequest
import com.thondigital.nc.application.model.request.RefreshTokenRequest
import com.thondigital.nc.application.model.request.ResetPasswordRequest
import com.thondigital.nc.application.model.request.RevokeTokenRequest
import com.thondigital.nc.application.model.request.SignInRequest
import com.thondigital.nc.application.model.request.SignUpRequest
import com.thondigital.nc.application.model.request.UpdatePasswordRequest
import com.thondigital.nc.application.model.response.generateHttpResponse
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import org.koin.ktor.ext.inject

fun Route.authApi() {
    val authController by inject<AuthController>()

    route("/auth") {
        authenticate {
            route("/idp") {
                // Single endpoint for both signing and registration
                post("/google") {
                    val idpAuthenticationRequest = call.receive<IdpAuthenticationRequest>()
                    val idpAuthenticationResponse =
                        authController.idpAuthentication(idpAuthenticationRequest, this.context)
                    val response = generateHttpResponse(idpAuthenticationResponse)
                    call.respond(response.code, response.body)
                }
            }
        }

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
                            this.context
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