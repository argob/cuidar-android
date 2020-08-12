package ar.gob.coronavirus.flujos.identificacion

import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales.convertirUsuario
import ar.gob.coronavirus.data.LocalToRemoteMapper.mapLocalToRemoteAddress
import ar.gob.coronavirus.data.LocalToRemoteMapper.mapLocalToRemoteLocation
import ar.gob.coronavirus.data.local.UserDAO
import ar.gob.coronavirus.data.local.modelo.LocalAddress
import ar.gob.coronavirus.data.local.modelo.LocalLocation
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.modelo.RemoteUser
import ar.gob.coronavirus.flujos.inicio.TokenRefreshStatus
import ar.gob.coronavirus.utils.PreferencesManager.getHash
import ar.gob.coronavirus.utils.PreferencesManager.getRefreshToken
import ar.gob.coronavirus.utils.extensions.applySchedulers
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.UnknownHostException

class IdentificationRepository(private val api: Api, private val userDao: UserDAO) {

    fun authorizeUser(dni: String, gender: String, tramitNumber: String): Boolean {
        val token = api.autorizarUsuario(dni, gender, tramitNumber)
        return token?.refreshToken?.isNotEmpty() ?: false
    }

    fun getUser(): Single<LocalUser> {
        return userDao.select()
                .applySchedulers()
    }

    fun registerUser(dni: String, gender: String): Single<LocalUser> {
        return Single
                .fromCallable { api.getUserInformation(dni, gender) }
                .flatMap { remoteUser: RemoteUser -> Single.fromCallable { convertirUsuario(remoteUser) } }
                .flatMap { localUser: LocalUser ->
                    Single.fromCallable {
                        val userWasInserted = userDao.insert(localUser) > -1
                        if (userWasInserted) {
                            return@fromCallable localUser
                        }
                        throw Exception("Error inserting local user")
                    }
                }
                .doOnError { t: Throwable? -> Timber.e(t) }
                .applySchedulers()
    }

    fun updateUser(dni: String, gender: String, phone: String, localAddress: LocalAddress, localLocation: LocalLocation?): Completable {
        return Single.fromCallable {
            val address = mapLocalToRemoteAddress(localAddress)
            val location = mapLocalToRemoteLocation(localLocation)
            val remoteUser = api.updateUser(dni, gender, phone, address, location)
            convertirUsuario(remoteUser)
        }.flatMapCompletable { localUser: LocalUser ->
            Completable.fromCallable {
                val userWasUpdated = userDao.update(localUser) > 0
                if (!userWasUpdated) {
                    throw Exception("Error updating local user")
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun refreshToken(): Single<TokenRefreshStatus> {
        return api.refresh(getRefreshToken(), getHash())
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