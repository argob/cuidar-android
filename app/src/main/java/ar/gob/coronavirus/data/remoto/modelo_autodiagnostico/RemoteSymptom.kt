package ar.gob.coronavirus.data.remoto.modelo_autodiagnostico

import com.google.gson.annotations.SerializedName

data class RemoteSymptom(
        @SerializedName("id")
        val id: String,
        @SerializedName("descripcion")
        val description: String,
        @SerializedName("valor")
        var value: Boolean)