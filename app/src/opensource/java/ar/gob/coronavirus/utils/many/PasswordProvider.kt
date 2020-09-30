package ar.gob.coronavirus.utils.many

import ar.gob.coronavirus.utils.PreferencesManager

object PasswordProvider {
    fun getPassword() = ("password"
            .also {
                PreferencesManager.savePassword(it)
            }).toCharArray()
}