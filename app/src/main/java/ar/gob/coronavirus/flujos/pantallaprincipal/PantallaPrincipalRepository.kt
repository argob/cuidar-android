package ar.gob.coronavirus.flujos.pantallaprincipal

import android.util.Log
import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales.convertirUsuario
import ar.gob.coronavirus.data.local.UserDAO
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.remoto.AdviceService
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.modelo.AdviceCount
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.extensions.applySchedulers
import ar.gob.coronavirus.utils.many.TextUtils
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import kotlin.random.Random

class PantallaPrincipalRepository(private val api: Api, private val userDao: UserDAO, private val adviceService: AdviceService) {

    fun getAdviceUrl(): Single<String> {
        return Single.zip(adviceService.requestAdviceCount(), userDao.select(), BiFunction<AdviceCount, LocalUser, String> { advices, user ->
            val province = user.address?.province
            if (PreferencesManager.wasLastShownAdviceNation() && !province.isNullOrEmpty() && advices.provinces?.containsKey(province) == true) {
                "${TextUtils.ADVICE_URL}${advices.provinces[province]?.directory ?: ""}consejo${Random.nextInt(1, advices.provinces[province]?.quantity ?: 1)}.svg"
            } else {
                "${TextUtils.ADVICE_URL}consejo${Random.nextInt(1, advices.quantity)}.svg"
            }.also {
                PreferencesManager.saveWasLastShownAdviceNation(!PreferencesManager.wasLastShownAdviceNation())
                Log.d("MY_TAG", "Loading URL $it")
            }
        }).applySchedulers()
    }

    fun updateUser(): Single<LocalUser> {
        return userDao.select().flatMap { localUser ->
            Single.fromCallable {
                api.obtenerUsuario(localUser.dni.toString(), localUser.gender)?.run {
                    convertirUsuario(this)
                } ?: localUser
            }
        }.flatMap { newUser ->
            Single.fromCallable {
                val userWasUpdated = userDao.update(newUser) > 0
                if (userWasUpdated) {
                    newUser
                } else {
                    throw Exception("Error updating local user")
                }
            }
        }.applySchedulers()
    }

    fun loadUser() = userDao.select().applySchedulers()
}