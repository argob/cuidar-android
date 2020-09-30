package ar.gob.coronavirus.data.local.modelo

import androidx.room.Embedded
import ar.gob.coronavirus.data.UserStatus

data class LocalState(
        val userStatus: UserStatus,
        val expirationDate: String,
        @Embedded
        val coep: LocalCoep,
        @Embedded(prefix = "pims_")
        val pims: LocalPims?)