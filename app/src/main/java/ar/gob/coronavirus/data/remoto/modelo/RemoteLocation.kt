package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class RemoteLocation(
        @SerializedName("latitud")
        val latitude: String,
        @SerializedName("longitud")
        val longitude: String
)