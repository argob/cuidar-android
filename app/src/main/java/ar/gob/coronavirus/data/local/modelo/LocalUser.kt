package ar.gob.coronavirus.data.local.modelo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "users")
data class LocalUser(
        @PrimaryKey
        val dni: Long,
        val gender: String,
        val birthDate: String,
        val names: String,
        val lastNames: String,
        val phone: String?,
        @Embedded
        val address: LocalAddress?,
        @Embedded
        val location: LocalLocation?,
        @Embedded
        val currentState: LocalState
)

data class UserWithPermits(
        @Embedded
        val user: LocalUser,
        @Relation(parentColumn = "dni", entityColumn = "userId")
        val permits: List<LocalCirculationPermit>
)