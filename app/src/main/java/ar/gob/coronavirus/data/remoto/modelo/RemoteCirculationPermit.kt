package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class RemoteCirculationPermit(
        @SerializedName("url-qr")
        val url: String,
        @SerializedName("fecha-vencimiento-permiso")
        val permitExpirationDate: String = "",
        @SerializedName("tipo-actividad")
        val activityType: String,
        @SerializedName("sube")
        val sube: String?,
        @SerializedName("patente")
        val plate: String?,
        @SerializedName("id-certificado")
        val certificateId: Int,
        @SerializedName("motivo-circulacion")
        val reason: String
)

/*
    "permiso-circulacion" : {
      "permiso-qr" : "string",
      "fecha-vencimiento-permiso" : "string",
      "status-servicio" : 0
    }
 */
