package ar.gob.coronavirus.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ar.gob.coronavirus.CovidApplication
import ar.gob.coronavirus.data.local.modelo.LocalCirculationPermit
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.many.PasswordProvider
import com.newrelic.agent.android.NewRelic
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File

@Database(entities = [LocalUser::class, LocalCirculationPermit::class], version = 4)
@TypeConverters(Converters::class)
abstract class EncryptedDataBase : RoomDatabase() {

    companion object {
        @JvmStatic
        val instance by lazy {
            try {
                createDatabase()
            } catch (e: Exception) {
                if (NewRelic.isStarted()) {
                    NewRelic.recordHandledException(e) // We want more information about this random crash
                }
                deleteDatabaseFile()
                createDatabase()
            }
        }

        private fun deleteDatabaseFile() {
            PreferencesManager.savePassword(null) // Clear the password so we get a new one
            val databaseDir = CovidApplication.instance.applicationInfo.dataDir.run { File(this, "databases") }
            if (databaseDir.exists() && databaseDir.isDirectory) {
                databaseDir.deleteRecursively()
            }
        }

        private fun createDatabase(): EncryptedDataBase {
            val passphrase = SQLiteDatabase.getBytes(PasswordProvider.getPassword())
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(CovidApplication.instance, EncryptedDataBase::class.java, "pasaporte_sanitario_v2.db")
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()
        }
    }

    abstract val userDao: UserDAO
    abstract val permitsDao: PermitsDao
}