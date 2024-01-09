package digital.thon.nc.application.controller.auth

import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import digital.thon.nc.application.auth.TokenProvider
import digital.thon.nc.application.auth.principal.UserPrincipal
import digital.thon.nc.application.controller.BaseController
import digital.thon.nc.application.exception.BadRequestException
import digital.thon.nc.application.exception.UnauthorizedActivityException
import digital.thon.nc.application.model.request.RefreshTokenRequest
import digital.thon.nc.application.model.request.RevokeTokenRequest
import digital.thon.nc.application.model.request.SignInRequest
import digital.thon.nc.application.model.request.SignUpRequest
import digital.thon.nc.application.model.request.UpdatePasswordRequest
import digital.thon.nc.application.model.response.AccountResponse
import digital.thon.nc.application.model.response.AuthResponse
import digital.thon.nc.application.model.response.GeneralResponse
import digital.thon.nc.application.model.response.Response
import digital.thon.nc.data.dao.TokenDao
import digital.thon.nc.data.dao.UserDao
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultAuthController : BaseController(), AuthController, KoinComponent {
    private val userDao by inject<UserDao>()
    private val refreshTokensDao by inject<TokenDao>()
    private val tokenProvider by inject<TokenProvider>()

    override suspend fun signIn(signInRequest: SignInRequest): Response {
        return try {
            validateSignInFieldsOrThrowException(signInRequest)
            userDao.findByEmail(signInRequest.email)?.let {
                verifyPasswordOrThrowException(signInRequest.password, it)
                val tokens = tokenProvider.createTokens(it)
                AuthResponse.success(
                    "Login realizado com sucesso",
                    tokens.accessToken,
                    tokens.refreshToken,
                )
            } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.unauthorized(e.message)
        }
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): Response {
        return try {
            validateSignUpFieldsOrThrowException(signUpRequest)
            verifyEmail(signUpRequest.email)
            val encryptedPassword = getEncryptedPassword(signUpRequest.password)
            val user = userDao.storeUser(signUpRequest.email, signUpRequest.username, encryptedPassword, false)
            val tokens = tokenProvider.createTokens(user)
            AuthResponse.success(
                "Registro realizado com sucesso",
                tokens.accessToken,
                tokens.refreshToken,
            )
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        }
    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Response {
        return try {
            val token = refreshTokenRequest.token
            val expirationTime = getConvertedTokenExpirationTime(token)
            val tokenType = getTokenType(token)
            validateRefreshTokenFieldsOrThrowException(token)
            tokenProvider.verifyToken(token)?.let { userId ->
                validateRefreshTokenType(tokenType)
                verifyTokenRevocation(token, userId)
                deleteExpiredTokens(userId, getConvertedCurrentTime())
                userDao.findByID(userId)?.let {
                    val tokens = tokenProvider.createTokens(it)
                    refreshTokensDao.store(userId, token, expirationTime)
                    AuthResponse.success(
                        "Tokens atualizados",
                        tokens.accessToken,
                        tokens.refreshToken,
                    )
                } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
            } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
        } catch (e: TokenExpiredException) {
            GeneralResponse.failed("Autenticação falhou: Refresh token expirado")
        } catch (e: SignatureVerificationException) {
            GeneralResponse.failed("Autenticação falhou: Falha ao analisar o Refresh token")
        } catch (e: JWTDecodeException) {
            GeneralResponse.failed("Autenticação falhou: Falha ao analisar o Refresh token")
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.unauthorized(e.message)
        }
    }

    override suspend fun revokeToken(revokeTokenRequest: RevokeTokenRequest): Response {
        return try {
            val token = revokeTokenRequest.token
            validateSignOutFieldsOrThrowException(token)
            tokenProvider.verifyToken(token)?.let { userId ->
                val tokenType = getTokenType(token)
                validateRefreshTokenType(tokenType)
                verifyTokenRevocation(token, userId)
                deleteExpiredTokens(userId, getConvertedCurrentTime())
                userDao.findByID(userId)?.let {
                    storeToken(token)
                    GeneralResponse.success(
                        "Logout realizado com sucesso",
                    )
                } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
            } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
        } catch (e: TokenExpiredException) {
            GeneralResponse.success("Revogação realizada com sucesso: Refresh token já expirado")
        } catch (e: SignatureVerificationException) {
            GeneralResponse.success("Revogação falhou: Falha ao analisar o Refresh token")
        } catch (e: JWTDecodeException) {
            GeneralResponse.success("Revogação falhou: Falha ao analisar o Refresh token")
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.unauthorized(e.message)
        }
    }

    override suspend fun getAccountById(ctx: ApplicationCall): Response {
        return try {
            val userId = ctx.principal<UserPrincipal>()?.user?.id
            userDao.findByID(userId!!)?.let {
                AccountResponse.success("Usuário encontrado", it.id, it.username, it.email)
            } ?: throw UnauthorizedActivityException("Usuário não existe")
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.notFound(e.message)
        }
    }

    override suspend fun updateAccountPassword(
        updatePasswordRequest: UpdatePasswordRequest,
        ctx: ApplicationCall,
    ): Response {
        return try {
            val userId = ctx.principal<UserPrincipal>()?.user?.id
            val encryptedPassword = getEncryptedPassword(updatePasswordRequest.currentPassword)
            validateUpdatePasswordFieldsOrThrowException(updatePasswordRequest)
            userDao.findByID(userId!!)?.let { user ->
                verifyPasswordOrThrowException(updatePasswordRequest.currentPassword, user)
                userDao.updatePassword(user.id, encryptedPassword)
                GeneralResponse.success(
                    "Senha atualizada",
                )
            } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.failed(e.message)
        }
    }

    override suspend fun resetPassword(userEmail: String): Response {
        return try {
            validateResetPasswordFieldsOrThrowException(userEmail)
            userDao.findByEmail(userEmail)?.let {
                val token = tokenProvider.createTokens(it)
                val email = SimpleEmail()
                email.hostName = "smtp-mail.outlook.com"
                email.setSmtpPort(587)
                email.setAuthenticator(
                    DefaultAuthenticator(
                        "",
                        "",
                    ),
                )
                email.isStartTLSEnabled = true
                email.setFrom("")
                email.subject = "Reset de senha!"
                email.setMsg(
                    "Para prosseguir com o processo de reset de senha, " +
                        "por favor, clique aqui: \n https://nc-server-332d35e07665.herokuapp.com/" +
                        "auth/confirm-reset-password?token=${token.accessToken}",
                )
                email.addTo("")
                email.send()
                GeneralResponse.success(
                    "Solicitação para redefinir a senha recebida. Verifique sua caixa de entrada para o link de redefinição.",
                )
            } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.unauthorized(e.message)
        }
    }

    override suspend fun confirmPasswordReset(
        tokenParameters: Parameters,
        updatePasswordRequest: UpdatePasswordRequest,
    ): Response {
        return try {
            val token = tokenParameters["token"]
            validateTokenParametersOrThrowException(token)
            tokenProvider.verifyToken(token!!)?.let { userId ->
                validateUpdatePasswordFieldsOrThrowException(updatePasswordRequest)
                val encryptedPassword = getEncryptedPassword(updatePasswordRequest.currentPassword)
                verifyTokenRevocation(token, userId)
                validateAccessTokenType(getTokenType(token))
                userDao.findByID(userId)?.let { user ->
                    verifyPasswordOrThrowException(updatePasswordRequest.currentPassword, user)
                    storeToken(token)
                    userDao.updatePassword(userId, encryptedPassword)
                    GeneralResponse.success(
                        "Senha atualizada",
                    )
                } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
            } ?: throw UnauthorizedActivityException("Autenticação falhou: Credenciais inválidas")
        } catch (e: TokenExpiredException) {
            GeneralResponse.failed("O link de redefinição foi revogado")
        } catch (e: SignatureVerificationException) {
            GeneralResponse.failed("Autenticação falhou: Falha ao analisar o token")
        } catch (e: JWTDecodeException) {
            GeneralResponse.failed("Autenticação falhou: Falha ao analisar o token")
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        } catch (e: UnauthorizedActivityException) {
            GeneralResponse.unauthorized(e.message)
        }
    }

    override suspend fun deleteAccount(ctx: ApplicationCall): Response {
        return try {
            if (ctx.principal<UserPrincipal>()?.user?.id == null) {
                GeneralResponse.failed("Autenticação falhou: Credenciais inválidas")
            } else {
                userDao.deleteUser(ctx.principal<UserPrincipal>()?.user?.id!!)
                GeneralResponse.success(
                    "Conta deletada com sucesso",
                )
            }
        } catch (e: BadRequestException) {
            GeneralResponse.failed(e.message)
        }
    }
}

interface AuthController {
    suspend fun signIn(signInRequest: SignInRequest): Response

    suspend fun signUp(signUpRequest: SignUpRequest): Response

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Response

    suspend fun revokeToken(revokeTokenRequest: RevokeTokenRequest): Response

    suspend fun getAccountById(ctx: ApplicationCall): Response

    suspend fun updateAccountPassword(
        updatePasswordRequest: UpdatePasswordRequest,
        ctx: ApplicationCall,
    ): Response

    suspend fun resetPassword(userEmail: String): Response

    suspend fun confirmPasswordReset(
        tokenParameters: Parameters,
        updatePasswordRequest: UpdatePasswordRequest,
    ): Response

    suspend fun deleteAccount(ctx: ApplicationCall): Response
}