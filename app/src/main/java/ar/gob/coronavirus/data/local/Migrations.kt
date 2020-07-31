package ar.gob.coronavirus.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

object MIGRATION_2_3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            Timber.d("Starting database migration...")
            migrateInternal(database)
        } catch (ex: Throwable) {
            Timber.e(ex, "Database migration failed due to: $ex.message")
        } finally {
            Timber.d("Completed database migration.")
        }
    }

    private fun migrateInternal(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE TABLE IF NOT EXISTS `new_users`(
            `dni` INTEGER PRIMARY KEY NOT NULL,
            `gender` TEXT NOT NULL, 
            `birthDate` TEXT NOT NULL, 
            `names` TEXT NOT NULL, 
            `lastNames` TEXT NOT NULL, 
            `phone` TEXT, 
            `sube` TEXT, 
            `plate` TEXT, 
            `province` TEXT, 
            `locality` TEXT, 
            `apartment` TEXT, 
            `street` TEXT, 
            `number` TEXT, 
            `floor` TEXT, 
            `door` TEXT, 
            `postalCode` TEXT, 
            `others` TEXT, 
            `latitude` TEXT, 
            `longitude` TEXT, 
            `userStatus` TEXT NOT NULL, 
            `expirationDate` TEXT NOT NULL, 
            `coep` TEXT NOT NULL, 
            `contactInformation` TEXT NOT NULL, 
            `qr` TEXT, 
            `permitExpirationDate` TEXT, 
            `serviceStatus` INTEGER, 
            `activityType` TEXT,
            `pims_tag` TEXT,
            `pims_reason` TEXT
            )""")

        database.execSQL("""INSERT INTO `new_users`(dni, gender, birthDate, names, lastNames, phone, sube, plate, province, locality, apartment, street, number, floor, door, postalCode, others, latitude, longitude, userStatus, expirationDate, coep, contactInformation, qr, permitExpirationDate, serviceStatus, activityType, pims_tag, pims_reason) 
            SELECT dni, gender, birthDate, names, lastNames, phone, sube, plate, province, locality, apartment, street, number, floor, door, postalCode, others, latitude, longitude, userStatus, expirationDate, coep, contactInformation, qr, permitExpirationDate, serviceStatus, activityType, pims_tag, pims_reason 
            FROM users""")

        database.execSQL("DROP TABLE users")
        database.execSQL("ALTER TABLE new_users RENAME TO users")
    }
}