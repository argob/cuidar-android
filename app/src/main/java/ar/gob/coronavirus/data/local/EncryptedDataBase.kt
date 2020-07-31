package ar.gob.coronavirus.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ar.gob.coronavirus.CovidApplication
import ar.gob.coronavirus.data.local.modelo.LocalUser
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [LocalUser::class], version = 3)
@TypeConverters(Converters::class)
abstract class EncryptedDataBase : RoomDatabase() {

    companion object {
        @JvmStatic
        val instance by lazy { createDatabase() }

        private fun createDatabase(): EncryptedDataBase {
            val passphrase = SQLiteDatabase.getBytes(PasswordProvider.getPassword())
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(CovidApplication.getInstance(), EncryptedDataBase::class.java, "pasaporte_sanitario_v2.db")
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()
        }
    }

    abstract val userDao: UserDAO
}