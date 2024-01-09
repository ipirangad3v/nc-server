package digital.thon.nc.application.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import digital.thon.nc.application.model.response.TokenResponse
import digital.thon.nc.data.model.User
import java.util.Date

object JWTController : TokenProvider {
    private const val SECRET = "bkFwb2xpdGE2OTk5"
    private const val ISSUER = "bkFwb2xpdGE2OTk5"
    private const val VALIDITY_IN_MILLIS: Long = 1200000L // 20 Minutes
    private const val REFRESH_VALIDITY_IN_MILLIS: Long = 3600000L * 24L * 30L // 30 days
    private val algorithm = Algorithm.HMAC512(SECRET)

    val verifier: JWTVerifier =
        JWT
            .require(algorithm)
            .withIssuer(ISSUER)
            .build()

    override fun verifyToken(token: String): Int? {
        return verifier.verify(token).claims["userId"]?.asInt()
    }

    override fun getTokenExpiration(token: String): Date {
        return verifier.verify(token).expiresAt
    }

    /**
     * Produce token and refresh token for this combination of User and Account
     */
    override fun createTokens(user: User) =
        TokenResponse(
            createAccessToken(user, getTokenExpiration()),
            createRefreshToken(user, getTokenExpiration(REFRESH_VALIDITY_IN_MILLIS)),
        )

    override fun verifyTokenType(token: String): String {
        return verifier.verify(token).claims["tokenType"]!!.asString()
    }

    private fun createAccessToken(
        user: User,
        expiration: Date,
    ) = JWT.create()
        .withSubject("Authentication")
        .withIssuer(ISSUER)
        .withClaim("userId", user.id)
        .withClaim("tokenType", "accessToken")
        .withExpiresAt(expiration)
        .sign(algorithm)

    private fun createRefreshToken(
        user: User,
        expiration: Date,
    ) = JWT.create()
        .withSubject("Authentication")
        .withIssuer(ISSUER)
        .withClaim("userId", user.id)
        .withClaim("tokenType", "refreshToken")
        .withExpiresAt(expiration)
        .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getTokenExpiration(validity: Long = VALIDITY_IN_MILLIS) = Date(System.currentTimeMillis() + validity)
}

interface TokenProvider {
    fun createTokens(user: User): TokenResponse

    fun verifyTokenType(token: String): String

    fun verifyToken(token: String): Int?

    fun getTokenExpiration(token: String): Date
}
