package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class UserInformationUpdate(
        @SerializedName("telefono")
        private val phone: String,
        @SerializedName("domicilio")
        private val address: RemoteAddress?,
        @SerializedName("geo-inicial")
        private val geo: RemoteLocation?)