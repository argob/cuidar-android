package ar.gob.coronavirus.data.local

import androidx.room.*
import ar.gob.coronavirus.data.local.modelo.LocalUser
import io.reactivex.Single

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: LocalUser): Long

    @Update
    fun update(user: LocalUser): Int

    @Query("SELECT * FROM users limit 1")
    fun select(): Single<LocalUser>

    @Query("DELETE FROM users")
    fun deleteAll()
}