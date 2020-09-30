package ar.gob.coronavirus.flujos.pantallaprincipal

import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales.convertirUsuario
import ar.gob.coronavirus.data.local.PermitsDao
import ar.gob.coronavirus.data.local.UserDAO
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.local.modelo.UserWithPermits
import ar.gob.coronavirus.data.remoto.AdviceService
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.modelo.AdviceCount
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.extensions.applySchedulers
import ar.gob.coronavirus.utils.many.APIConstants
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import timber.log.Timber
import kotlin.random.Random

class PantallaPrincipalRepository(private val api: Api, private val userDao: UserDAO, private val permitsDao: PermitsDao, private val adviceService: AdviceService) {

    fun getAdviceUrl(): Single<String> {
        return Single.zip(adviceService.requestAdviceCount().onErrorReturnItem(AdviceCount(0, null)), userDao.select(), BiFunction<AdviceCount, LocalUser, String> { advices, user ->
            if (advices.quantity == 0 && advices.provinces == null)
                return@BiFunction ""

            val province = user.address?.province
            if (PreferencesManager.wasLastShownAdviceNation() && !province.isNullOrEmpty() && advices.provinces?.containsKey(province) == true) {
                "${APIConstants.ADVICE_URL}${advices.provinces[province]?.directory ?: ""}consejo${Random.nextInt(1, advices.provinces[province]?.quantity ?: 1)}.svg"
            } else {
                "${APIConstants.ADVICE_URL}consejo${Random.nextInt(1, advices.quantity)}.svg"
            }.also {
                PreferencesManager.saveWasLastShownAdviceNation(!PreferencesManager.wasLastShownAdviceNation())
            }
        }).applySchedulers()
    }

    fun updateUser(): Single<UserWithPermits> {
        return userDao.selectWithPermits()
                .flatMap { (localUser, permits) ->
                    api.getUserInformation(localUser.dni.toString(), localUser.gender)
                            .map { convertirUsuario(it) }
                            .doOnError { Timber.e(it) }
                            .onErrorReturnItem(UserWithPermits(localUser, permits))
                }
                .flatMap { Completable.mergeArrayDelayError(userDao.update(it.user), permitsDao.save(it.permits)).doOnError { e -> Timber.e(e) }.toSingle { it } }
                .applySchedulers()
    }
    
    fun loadUser() = userDao.selectWithPermitsFlow().applySchedulers()
}