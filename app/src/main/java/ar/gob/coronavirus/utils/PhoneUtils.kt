package ar.gob.coronavirus.utils

object PhoneUtils {
    private const val PHONE_REGEX = "[0-9]{6,13}"

    @JvmStatic
    fun isValidPhone(phone: String?) = phone?.matches(PHONE_REGEX.toRegex()) == true
}