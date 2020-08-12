package ar.gob.coronavirus.data.remoto.modelo

import com.google.gson.annotations.SerializedName

data class PushBody(@SerializedName("id-app") val idApp: String)