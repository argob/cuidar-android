package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class RemoteAddress(
        @SerializedName("provincia")
        val province: String,
        @SerializedName("localidad")
        val locality: String,
        @SerializedName("calle")
        val street: String,
        @SerializedName("numero")
        val number: String,
        @SerializedName("piso")
        val floor: String?,
        @SerializedName("puerta")
        val door: String?,
        @SerializedName("codigo-postal")
        val postalCode: String,
        @SerializedName("otros")
        val others: String?,
        @SerializedName("departamento")
        val apartment: String?
)
/*
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
 */