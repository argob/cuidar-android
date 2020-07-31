package ar.gob.coronavirus.data.local.modelo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class LocalUser(
        @PrimaryKey
        val dni: Long,
        val gender: String,
        val birthDate: String,
        val names: String,
        val lastNames: String,
        val phone: String?,
        val sube: String?,
        val plate: String?,
        @Embedded
        val address: LocalAddress?,
        @Embedded
        val location: LocalLocation?,
        @Embedded
        val currentState: LocalState
)