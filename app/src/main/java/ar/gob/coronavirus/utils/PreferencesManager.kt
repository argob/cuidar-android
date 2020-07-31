package ar.gob.coronavirus.utils

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ar.gob.coronavirus.CovidApplication

object PreferencesManager {

    private val context by lazy { CovidApplication.getInstance() }
    private val masterKey by lazy {
        MasterKey.Builder(CovidApplication.getInstance())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
    }

    private val encryptedSharedPreferences by lazy {
        EncryptedSharedPreferences
                .create(context,
                        Constantes.PREFS_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    private val sharedPreferences by lazy { context.getSharedPreferences("preferences", Context.MODE_PRIVATE) }


    // Refers to the refresh token
    fun saveRefreshToken(header: String) {
        encryptedSharedPreferences.edit { putString(Constantes.HEADER, header) }
    }

    // Refers to the refresh token
    fun getRefreshToken() = encryptedSharedPreferences.getString(Constantes.HEADER, null)

    // Refers to the jashh
    fun saveHash(something: String) {
        encryptedSharedPreferences.edit { putString(Constantes.SOMETHING, something) }
    }

    // Refers to the jashh
    fun getHash() = encryptedSharedPreferences.getString(Constantes.SOMETHING, null)

    fun getPassword(): String? = encryptedSharedPreferences.getString(Constantes.PASSWORD, null)

    fun savePassword(password: String) = encryptedSharedPreferences.edit { putString(Constantes.PASSWORD, password) }

    fun saveWasLastShownAdviceNation(was: Boolean) = sharedPreferences.edit { putBoolean(Constantes.LAST_SHOWN_ADVICE, was) }

    fun wasLastShownAdviceNation() = sharedPreferences.getBoolean(Constantes.LAST_SHOWN_ADVICE, false)

    fun clear() {
        encryptedSharedPreferences.edit {
            val password = encryptedSharedPreferences.getString(Constantes.PASSWORD, null)
            clear()
            putString(Constantes.PASSWORD, password)
        }
    }
}