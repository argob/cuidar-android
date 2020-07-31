package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class RemoteCoep(
        val coep: String = "",
        @SerializedName("informacionDeContacto")
        val contactInformation: String = ""
)

/*
     "datos-coep": {
      "coep": "string",
      "informacionDeContacto": "string"
    },
 */