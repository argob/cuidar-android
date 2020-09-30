package ar.gob.coronavirus.flujos.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.gob.coronavirus.data.UserStatus
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.repositorios.LogoutRepository
import ar.gob.coronavirus.flujos.BaseViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificationRepository
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.extensions.applySchedulers
import ar.gob.coronavirus.utils.extensions.delayedSingle
import ar.gob.coronavirus.utils.observables.Event
import io.reactivex.Single
import io.reactivex.functions.Function3
import java.util.concurrent.TimeUnit

class SplashViewModel(identificationRepository: IdentificationRepository, logoutRepositoryRepository: LogoutRepository) : BaseViewModel() {

    private val _navigationLiveData = MutableLiveData<Event<SplashDestinations>>()
    val navigationLiveData: LiveData<Event<SplashDestinations>>
        get() = _navigationLiveData

    init {
        if (PreferencesManager.getRefreshToken().isNullOrEmpty() ||
                PreferencesManager.getHash().isNullOrEmpty() ||
                PreferencesManager.getPassword().isNullOrEmpty()) {
            delayedSingle(SplashDestinations.LOGIN, 2)
        } else {
            Single.zip(identificationRepository.getUser(),
                    identificationRepository.refreshToken().timeout(5, TimeUnit.SECONDS),
                    delayedSingle(true, 2), // Used to ensure that the single takes at least to seconds
                    Function3<LocalUser, TokenRefreshStatus, Boolean, SplashDestinations> { user, tokenStatus, _ ->
                        when {
                            tokenStatus == TokenRefreshStatus.INVALID -> SplashDestinations.LOGIN_INVALID
                            user.address?.province.isNullOrEmpty() || tokenStatus == TokenRefreshStatus.FAILED -> SplashDestinations.LOGIN
                            user.currentState.userStatus == UserStatus.MUST_SELF_DIAGNOSE -> SplashDestinations.DIAGNOSTICO
                            else -> SplashDestinations.PRINCIPAL
                        }
                    })
        }
                .flatMap {
                    // If login is invalid first clear everything, then take them to login
                    if (it == SplashDestinations.LOGIN_INVALID) {
                        logoutRepositoryRepository.logout().toSingle { it }
                    } else {
                        Single.just(it)
                    }
                }
                .applySchedulers()
                .subscribe({
                    _navigationLiveData.value = Event(it)
                }, {
                    _navigationLiveData.value = Event(SplashDestinations.LOGIN)
                }).also { addDisposable(it) }
    }

}

enum class TokenRefreshStatus {
    REFRESHED,
    INVALID,
    FAILED
}