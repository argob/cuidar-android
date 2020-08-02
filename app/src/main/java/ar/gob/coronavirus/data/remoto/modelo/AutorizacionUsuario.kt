package ar.gob.coronavirus.data.remoto.modelo

import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.many.ApiConstants
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

class AutorizacionUsuario(private val dni: String, private val sexo: String, private val tramite: String) {
    private val hash: String

    init {
        hash = computeHash()
    }

    private fun computeHash(): String = buildString {
        append(ApiConstants.getSecretAutorizacionv2())
        append('-')
        append(dni)
        append('-')
        append(tramite)
        append('-')
        append(sexo)
    }.run {
        String(Hex.encodeHex(DigestUtils.sha256(this)))
    }.also {
        PreferencesManager.saveHash(it)
    }
}