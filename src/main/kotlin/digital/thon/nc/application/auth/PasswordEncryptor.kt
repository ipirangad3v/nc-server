package digital.thon.nc.application.auth

import org.mindrot.jbcrypt.BCrypt

object PasswordEncryptor : PasswordEncryptorContract {
    private const val LETTERS: String = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE_LETTERS: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val NUMBERS: String = "0123456789"
    private const val SPECIAL: String = "@#=+!£$%&?"
    //  see evaluatePassword function below

    override fun validatePassword(
        attempt: String,
        userPassword: String,
    ): Boolean {
        return BCrypt.checkpw(attempt, userPassword)
    }

    override fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}

interface PasswordEncryptorContract {
    fun validatePassword(
        attempt: String,
        userPassword: String,
    ): Boolean

    fun encryptPassword(password: String): String
}