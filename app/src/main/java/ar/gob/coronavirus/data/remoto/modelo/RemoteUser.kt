package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class RemoteUser(
        @SerializedName("dni")
        val dni: Long,
        @SerializedName("sexo")
        val gender: String,
        @SerializedName("fecha-nacimiento")
        val birthDate: String,
        @SerializedName("nombres")
        val names: String,
        @SerializedName("apellidos")
        val lastNames: String,
        @SerializedName("telefono")
        val phone: String?,
        @SerializedName("domicilio")
        val address: RemoteAddress?,
        @SerializedName("geo-inicial")
        val location: RemoteLocation?,
        @SerializedName("estado-actual")
        val currentState: RemoteStatus?
)

/*
{
  "dni" : 0,
  "sexo" : "string",
  "fecha-nacimiento" : "string",
  "nombres" : "string",
  "apellidos" : "string",
  "telefono" : "string",
  "domicilio" : {
    "provincia" : "string",
    "localidad" : "string",
    "calle" : "string",
    "numero" : "string",
    "piso" : "string",
    "puerta" : "string",
    "codigo-postal" : "string",
    "otros" : "string"
  },
  "geo-inicial" : {
    "latitud" : "string",
    "longitud" : "string",
    "altura" : "string"
  },
  "estado-actual" : {
    "nombre-estado" : "string",
    "fecha-hora-vencimiento" : "string",
    "datos-coep" : "string",
    "permiso-circulacion" : {
      "permiso-qr" : "string",
      "fecha-vencimiento-permiso" : "string",
      "status-servicio" : 0
    },
    "pims"{
        "tag": "string",
        "reason": "string"
    }
  }
}
 */