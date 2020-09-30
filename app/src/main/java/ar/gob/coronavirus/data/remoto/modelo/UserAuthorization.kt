package ar.gob.coronavirus.data.remoto.modelo

import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.many.APIConstants
import com.google.gson.annotations.SerializedName
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

class UserAuthorization(
        @SerializedName("dni")
        private val dni: String,
        @SerializedName("sexo")
        private val gender: String,
        @SerializedName("tramite")
        private val identification: String) {
    private val hash: String

    init {
        hash = computeHash()
    }

    private fun computeHash(): String = buildString {
        append(APIConstants.getSecretAutorizacionv2())
        append('-')
        append(dni)
        append('-')
        append(identification)
        append('-')
        append(gender)
    }.run {
        String(Hex.encodeHex(DigestUtils.sha256(this)))
    }.also {
        PreferencesManager.saveHash(it)
    }
}