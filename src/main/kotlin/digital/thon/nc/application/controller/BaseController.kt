package digital.thon.nc.application.controller

import digital.thon.nc.application.auth.PasswordEncryptorContract
import digital.thon.nc.application.auth.TokenProvider
import digital.thon.nc.application.exception.BadRequestException
import digital.thon.nc.application.exception.UnauthorizedActivityException
import digital.thon.nc.application.model.request.SignInRequest
import digital.thon.nc.application.model.request.SignUpRequest
import digital.thon.nc.application.model.request.UpdatePasswordRequest
import digital.thon.nc.application.model.response.AuthResponse
import digital.thon.nc.application.utils.isEmailValid
import digital.thon.nc.application.utils.isValidName
import digital.thon.nc.data.dao.TokenDao
import digital.thon.nc.data.dao.UserDao
import digital.thon.nc.data.model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat

abstract class BaseController : KoinComponent {
    private val userDao by inject<UserDao>()
    private val refreshTokensDao by inject<TokenDao>()
    private val passwordEncryption by inject<PasswordEncryptorContract>()
    private val tokenProvider by inject<TokenProvider>()
    private val simpleDateFormat = SimpleDateFormat("'Date: 'yyyy-MM-dd' Time: 'HH:mm:ss")

    internal fun validateSignInFieldsOrThrowException(signInRequest: SignInRequest) {
        val message =
            when {
                (
                    signInRequest.email.isBlank() or
                        (signInRequest.password.isBlank())
                ) -> "Os campos de credenciais não devem estar em branco"

                (!signInRequest.email.isEmailValid()) -> "E-mail inválido"
                (signInRequest.password.length !in (8..50)) -> "A senha deve ter entre 8 e 50 caracteres de comprimento"
                else -> return
            }

        throw BadRequestException(message)
    }

    internal fun validateSignUpFieldsOrThrowException(signUpRequest: SignUpRequest) {
        val message =
            when {
                (
                    signUpRequest.email.isBlank() or
                        (signUpRequest.username.isBlank()) or
                        (signUpRequest.password.isBlank()) or
                        (signUpRequest.confirmPassword.isBlank())
                ) -> "Os campos não devem estar em branco"

                (!signUpRequest.email.isEmailValid()) -> "E-mail inválido"
                (!signUpRequest.username.isValidName()) -> "Nenhum caractere especial é permitido no nome de usuário"
                (signUpRequest.username.length !in (4..30)) -> "O nome de usuário deve ter entre 4 e 30 caracteres de comprimento"
                (signUpRequest.password.length !in (8..50)) -> "A senha deve ter entre 8 e 50 caracteres de comprimento"
                (signUpRequest.confirmPassword.length !in (8..50)) -> "A senha deve ter entre 8 e 50 caracteres de comprimento"
                (signUpRequest.password != signUpRequest.confirmPassword) -> "As senhas não coincidem"
                else -> return
            }

        throw BadRequestException(message)
    }

    internal fun validateResetPasswordFieldsOrThrowException(email: String) {
        val message =
            when {
                (email.isBlank()) -> "O campo de e-mail não pode estar vazio"
                else -> return
            }

        throw BadRequestException(message)
    }

    internal fun validateUpdatePasswordFieldsOrThrowException(updatePasswordRequest: UpdatePasswordRequest) {
        val message =
            when {
                (
                    updatePasswordRequest.currentPassword.isBlank() || updatePasswordRequest.newPassword.isBlank() ||
                        updatePasswordRequest.confirmNewPassword.isBlank()
                ) -> {
                    "O campo de senha não pode estar vazio"
                }

                updatePasswordRequest.newPassword != updatePasswordRequest.confirmNewPassword -> "As senhas não coincidem"
                updatePasswordRequest.newPassword.length !in (8..50) -> "A senha deve ter entre 8 e 50 caracteres de comprimento"
                updatePasswordRequest.confirmNewPassword.length !in (8..50) -> "A senha deve ter entre 8 e 50 caracteres de comprimento"
                else -> return
            }

        throw BadRequestException(message)
    }

    internal fun validateTokenParametersOrThrowException(token: String?) {
        if (token == null) throw BadRequestException("Parâmetro de consulta de token ausente")
    }

    internal fun validateRefreshTokenFieldsOrThrowException(token: String) {
        val message =
            when {
                (token.isBlank()) -> "Falha na autenticação: O campo de token não deve estar em branco"
                else -> return
            }

        throw BadRequestException(message)
    }

    internal fun validateSignOutFieldsOrThrowException(token: String) {
        val message =
            when {
                (token.isBlank()) -> "Falha na autenticação: O campo de token não deve estar em branco"
                else -> return
            }
        throw BadRequestException(message)
    }

    internal fun validateRefreshTokenType(tokenType: String) {
        if (tokenType != "refreshToken") throw BadRequestException("Falha na autenticação: Tipo de token inválido")
    }

    internal fun validateAccessTokenType(tokenType: String) {
        if (tokenType != "accessToken") throw BadRequestException("Falha na autenticação: Tipo de token inválido")
    }

    internal suspend fun verifyTokenRevocation(
        token: String,
        userId: Int,
    ) {
        if (refreshTokensDao.exists(
                userId,
                token,
            )
        ) {
            throw UnauthorizedActivityException("Falha na autenticação: O token foi revogado")
        }
    }

    internal fun verifyPasswordOrThrowException(
        password: String,
        user: User,
    ) {
        user.password?.let {
            if (!passwordEncryption.validatePassword(password, it)) {
                throw UnauthorizedActivityException("Falha na autenticação: Credenciais inválidas")
            }
        } ?: throw UnauthorizedActivityException("Falha na autenticação: Credenciais inválidas")
    }

    internal suspend fun storeToken(token: String) {
        val expirationTime = tokenProvider.getTokenExpiration(token)
        val convertedExpirationTime = simpleDateFormat.format(expirationTime)

        try {
            tokenProvider.verifyToken(token)?.let { userId ->
                userDao.findByID(userId)?.let {
                    refreshTokensDao.store(
                        it.id,
                        token,
                        convertedExpirationTime,
                    )
                } ?: throw UnauthorizedActivityException("Falha na autenticação: Credenciais inválidas")
            } ?: throw UnauthorizedActivityException("Falha na autenticação: Credenciais inválidas")
        } catch (uae: UnauthorizedActivityException) {
            AuthResponse.unauthorized(uae.message)
        }
    }

    internal suspend fun deleteExpiredTokens(
        userId: Int,
        currentTime: String,
    ) {
        refreshTokensDao.getAllById(userId).let { tokens ->
            tokens.forEach {
                if (it.expirationTime < currentTime) {
                    refreshTokensDao.deleteById(it.id)
                }
            }
        }
    }

    internal suspend fun verifyEmail(email: String) {
        if (!userDao.isEmailAvailable(email)) {
            throw BadRequestException("Falha na autenticação: E-mail já cadastrado")
        }
    }

    internal fun getEncryptedPassword(password: String): String {
        return passwordEncryption.encryptPassword(password)
    }

    internal fun getConvertedTokenExpirationTime(token: String): String {
        val expirationTime = tokenProvider.getTokenExpiration(token)
        return simpleDateFormat.format(expirationTime)
    }

    internal fun getConvertedCurrentTime(): String = simpleDateFormat.format((System.currentTimeMillis()))

    internal fun getTokenType(token: String): String {
        return tokenProvider.verifyTokenType(token)
    }
}