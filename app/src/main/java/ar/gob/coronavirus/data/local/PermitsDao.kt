package ar.gob.coronavirus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.gob.coronavirus.data.local.modelo.LocalCirculationPermit
import io.reactivex.Completable

@Dao
interface PermitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(permits: List<LocalCirculationPermit>): Completable

    @Query("DELETE FROM permits")
    fun clear(): Completable

}