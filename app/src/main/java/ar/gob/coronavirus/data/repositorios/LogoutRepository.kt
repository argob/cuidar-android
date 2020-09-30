package ar.gob.coronavirus.data.repositorios

import ar.gob.coronavirus.data.local.PermitsDao
import ar.gob.coronavirus.data.local.UserDAO
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.SharedUtils
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import timber.log.Timber

class LogoutRepository(private val api: Api, private val userDao: UserDAO, private val permitsDao: PermitsDao, private val sharedUtils: SharedUtils) {

    fun logout(): Completable {
        return userDao.select()
                .flatMapCompletable { (dni, sexo) ->
                    val token = sharedUtils.getString(Constantes.SHARED_KEY_PUSH)
                    sharedUtils.putString(Constantes.SHARED_KEY_PUSH, "")
                    api.unregisterPush(dni.toString(), sexo, token)
                }
                .onErrorResumeNext { userDao.deleteAll() }
                .andThen(userDao.deleteAll())
                .andThen(permitsDao.clear())
                .andThen(Completable.fromAction {
                    PreferencesManager.clear()
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                })
    }

}