package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class RemoteCirculationPermit(
        @SerializedName("permiso-qr")
        val qr: String = "",
        @SerializedName("fecha-vencimiento-permiso")
        val permitExpirationDate: String = "",
        @SerializedName("status-servicio")
        val serviceStatus: Int = -1,
        @SerializedName("tipo-actividad")
        val activityType: String? = null,
        @SerializedName("sube")
        val sube: String?,
        @SerializedName("patente")
        val plate: String?
)

/*
    "permiso-circulacion" : {
      "permiso-qr" : "string",
      "fecha-vencimiento-permiso" : "string",
      "status-servicio" : 0
    }
 */
