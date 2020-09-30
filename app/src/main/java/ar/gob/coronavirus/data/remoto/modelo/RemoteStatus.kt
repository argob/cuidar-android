package ar.gob.coronavirus.data.remoto.modelo

import ar.gob.coronavirus.data.UserStatus
import com.google.gson.annotations.SerializedName

data class RemoteStatus(
        @SerializedName("nombre-estado")
        val userStatus: UserStatus?,
        @SerializedName("fecha-hora-vencimiento")
        val expirationDate: String?,
        @SerializedName("datos-coep")
        val coep: RemoteCoep?,
        @SerializedName("permisos-circulacion")
        val circulationPermits: List<RemoteCirculationPermit>?,
        val pims: RemotePims?
)

