package ar.gob.coronavirus.flujos.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.gob.coronavirus.data.UserStatus
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.data.repositorios.RepositorioLogout
import ar.gob.coronavirus.flujos.BaseViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificationRepository
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.extensions.applySchedulers
import ar.gob.coronavirus.utils.extensions.delayedSingle
import ar.gob.coronavirus.utils.observables.EventoUnico
import io.reactivex.Single
import io.reactivex.functions.Function3

class SplashViewModel(identificationRepository: IdentificationRepository, logoutRepository: RepositorioLogout) : BaseViewModel() {

    private val _navigationLiveData = MutableLiveData<EventoUnico<NavegacionFragments>>()
    val navigationLiveData: LiveData<EventoUnico<NavegacionFragments>>
        get() = _navigationLiveData

    init {
        if (PreferencesManager.getRefreshToken().isNullOrEmpty() ||
                PreferencesManager.getHash().isNullOrEmpty() ||
                PreferencesManager.getPassword().isNullOrEmpty()) {
            delayedSingle(NavegacionFragments.LOGIN, 2)
        } else {
            Single.zip(identificationRepository.getUser(),
                    identificationRepository.refreshToken(),
                    delayedSingle(true, 2), // Used to ensure that the single takes at least to seconds
                    Function3<LocalUser, TokenRefreshStatus, Boolean, NavegacionFragments> { user, tokenStatus, _ ->
                        when {
                            tokenStatus == TokenRefreshStatus.INVALID -> NavegacionFragments.LOGIN_INVALID.also { logoutRepository.logout() }
                            user.address?.province.isNullOrEmpty() || tokenStatus == TokenRefreshStatus.FAILED -> NavegacionFragments.LOGIN
                            user.currentState.userStatus == UserStatus.MUST_SELF_DIAGNOSE -> NavegacionFragments.DIAGNOSTICO
                            else -> NavegacionFragments.PRINCIPAL
                        }
                    })
        }
                .applySchedulers()
                .subscribe({
                    _navigationLiveData.value = EventoUnico(it)
                }, {
                    _navigationLiveData.value = EventoUnico(NavegacionFragments.LOGIN)
                }).also { addDisposable(it) }
    }

}

enum class TokenRefreshStatus {
    REFRESHED,
    INVALID,
    FAILED
}