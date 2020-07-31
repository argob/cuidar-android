package ar.gob.coronavirus.data.local

import androidx.room.TypeConverter
import ar.gob.coronavirus.data.UserStatus

class Converters {
    @TypeConverter
    fun stringToStatus(string: String): UserStatus {
        return UserStatus.fromString(string)
    }

    @TypeConverter
    fun statusToString(status: UserStatus): String {
        return status.value
    }
}