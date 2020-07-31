package ar.gob.coronavirus.data.local

import ar.gob.coronavirus.utils.PreferencesManager

object PasswordProvider {
    fun getPassword() = (PreferencesManager.getPassword()
            ?: buildRandomString().also {
                PreferencesManager.savePassword(it)
            }).toCharArray()

    private fun buildRandomString(length: Int = 30): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
    }
}