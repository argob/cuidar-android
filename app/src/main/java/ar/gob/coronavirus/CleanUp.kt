package ar.gob.coronavirus

import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File

object CleanUp {
    private val compositeDisposable = CompositeDisposable()

    fun launch(dataDir: String) {
        Completable.fromAction {
            val dbsDirectory = File(dataDir, "databases")
            if (dbsDirectory.exists() && dbsDirectory.isDirectory) {
                File(dbsDirectory, "pasaporte_sanitario.db")
                        .takeIf { it.exists() }
                        ?.run { delete() }
                File(dbsDirectory, "pasaporte_sanitario.db-shm")
                        .takeIf { it.exists() }
                        ?.run { delete() }
                File(dbsDirectory, "pasaporte_sanitario.db-wal")
                        .takeIf { it.exists() }
                        ?.run { delete() }
            }
        }.doFinally {
            compositeDisposable.clear()
        }.subscribe(
                { Timber.d("DB Removed") },
                { Timber.e(it, "Error removing DB") }).also { compositeDisposable.add(it) }
    }
}