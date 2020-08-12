package ar.gob.coronavirus.data.remoto.modelo_autodiagnostico

import com.google.gson.annotations.SerializedName

data class RemoteAntecedents(@SerializedName("id") val id: String,
                             @SerializedName("descripcion")
                             val description: String,
                             @SerializedName("valor")
                             var value: Boolean)