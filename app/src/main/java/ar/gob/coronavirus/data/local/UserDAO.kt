package ar.gob.coronavirus.data.local

import androidx.room.*
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.local.modelo.UserWithPermits
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: LocalUser): Completable

    @Update
    fun update(user: LocalUser): Completable

    @Query("SELECT * FROM users limit 1")
    fun select(): Single<LocalUser>

    @Transaction
    @Query("SELECT * FROM users limit 1")
    fun selectWithPermitsFlow(): Flowable<UserWithPermits>

    @Transaction
    @Query("SELECT * FROM users limit 1")
    fun selectWithPermits(): Single<UserWithPermits>

    @Query("DELETE FROM users")
    fun deleteAll(): Completable
}