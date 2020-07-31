package ar.gob.coronavirus.data.local.modelo

data class LocalCirculationPermit(
        val qr: String,
        val permitExpirationDate: String,
        val serviceStatus: Int,
        val activityType: String)