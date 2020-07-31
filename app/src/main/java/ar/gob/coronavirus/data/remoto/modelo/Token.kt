package ar.gob.coronavirus.data.remoto.modelo

import ar.gob.coronavirus.utils.Constantes
import com.google.gson.annotations.SerializedName

data class Token(val token: String,
                 @SerializedName(Constantes.REFRESH_HEADER)
                 val refreshToken: String)

data class TokenRefreshBody(
        @SerializedName(Constantes.REFRESH_HEADER)
        val refreshToken: String,
        val hash: String)