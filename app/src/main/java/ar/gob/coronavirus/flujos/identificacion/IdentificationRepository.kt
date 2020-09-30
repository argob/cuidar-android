package ar.gob.coronavirus.flujos.identificacion

import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales.convertirUsuario
import ar.gob.coronavirus.data.LocalToRemoteMapper.mapLocalToRemoteAddress
import ar.gob.coronavirus.data.LocalToRemoteMapper.mapLocalToRemoteLocation
import ar.gob.coronavirus.data.local.PermitsDao
import ar.gob.coronavirus.data.local.UserDAO
import ar.gob.coronavirus.data.local.modelo.LocalAddress
import ar.gob.coronavirus.data.local.modelo.LocalLocation
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.local.modelo.UserWithPermits
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.modelo.RemoteUser
import ar.gob.coronavirus.flujos.inicio.TokenRefreshStatus
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.extensions.applySchedulers
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.UnknownHostException

class IdentificationRepository(private val api: Api, private val userDao: UserDAO, private val permitsDao: PermitsDao) {

    fun authorizeUser(dni: String, gender: String, identification: String): Single<Boolean> {
        return api.authorize(dni, gender, identification)
                .map { it.refreshToken.isNotEmpty() }
    }

    fun getUser(): Single<LocalUser> {
        return userDao.select()
                .applySchedulers()
    }

    fun registerUser(dni: String, gender: String): Single<LocalUser> {
        return api.getUserInformation(dni, gender)
                .flatMap { remoteUser: RemoteUser -> Single.fromCallable { convertirUsuario(remoteUser) } }
                .flatMap { userWithPermits: UserWithPermits ->
                    Completable
                            .mergeArrayDelayError(userDao.insert(userWithPermits.user), permitsDao.save(userWithPermits.permits))
                            .toSingle { userWithPermits.user }
                }
                .doOnError { t: Throwable? -> Timber.e(t) }
                .applySchedulers()
    }

    fun updateUser(dni: String, gender: String, phone: String, localAddress: LocalAddress, localLocation: LocalLocation?): Completable {
        val address = mapLocalToRemoteAddress(localAddress)
        val location = mapLocalToRemoteLocation(localLocation)
        return api.updateUser(dni, gender, phone, address, location)
                .map { convertirUsuario(it) }
                .flatMapCompletable { Completable.concatArray(userDao.update(it.user), permitsDao.save(it.permits)) }
                .applySchedulers()
    }

    fun refreshToken(): Single<TokenRefreshStatus> {
        return api.refresh(PreferencesManager.getRefreshToken()!!, PreferencesManager.getHash()!!)
                .flatMap { Single.just(TokenRefreshStatus.REFRESHED) }
                .onErrorReturn { throwable: Throwable? ->
                    when {
                        throwable is UnknownHostException -> TokenRefreshStatus.REFRESHED
                        throwable is HttpException && throwable.code() == HttpURLConnection.HTTP_UNAUTHORIZED -> TokenRefreshStatus.INVALID
                        else -> TokenRefreshStatus.FAILED
                    }
                }
    }

}