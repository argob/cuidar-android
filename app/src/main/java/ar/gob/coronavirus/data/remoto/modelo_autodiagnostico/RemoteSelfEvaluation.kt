package ar.gob.coronavirus.data.remoto.modelo_autodiagnostico

import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation
import com.google.gson.annotations.SerializedName

data class RemoteSelfEvaluation(
        @SerializedName("temperatura")
        var temperature: Double,
        @SerializedName("sintomas")
        val symptoms: MutableList<RemoteSymptom>,
        @SerializedName("antecedentes")
        val antecedents: MutableList<RemoteAntecedents>,
        @SerializedName("geo")
        var remoteLocation: RemoteLocation?)