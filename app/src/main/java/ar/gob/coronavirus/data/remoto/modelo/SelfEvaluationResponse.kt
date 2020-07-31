package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class SelfEvaluationResponse(
        val dni: Long,
        @SerializedName("sexo")
        val gender: String,
        @SerializedName("estado-actual")
        val currentState: RemoteStatus
)