package digital.thon.nc.application.utils

const val MAIL_REGEX = (
    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
        "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
        "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
        "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
        "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
        "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
)

fun String.isEmailValid(): Boolean = this.isNotBlank() && Regex(MAIL_REGEX).matches(this)

fun String.isValidName() = matches("^[a-zA-ZÀ-ÿ]+(?: [a-zA-ZÀ-ÿ]+)+$".toRegex())