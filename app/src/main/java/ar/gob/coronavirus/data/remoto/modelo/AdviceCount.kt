package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class AdviceCount(
        @SerializedName("cantidad")
        val quantity: Int,
        @SerializedName("provinciales")
        val provinces: Map<String, ProvinceAdvice>?
)

data class ProvinceAdvice(
        @SerializedName("cantidad")
        val quantity: Int,
        @SerializedName("directorio")
        val directory: String
)