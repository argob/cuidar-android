package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

class RemotePims(
        val tag: String?,
        @SerializedName("motivo")
        val reason: String?)