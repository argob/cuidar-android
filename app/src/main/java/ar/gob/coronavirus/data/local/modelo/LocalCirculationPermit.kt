package ar.gob.coronavirus.data.local.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permits")
data class LocalCirculationPermit(
        val userId: Long,
        val sube: String,
        val plate: String,
        val url: String,
        val permitExpirationDate: String,
        val activityType: String,
        val reason: String,
        @PrimaryKey
        val certificateId: Int)