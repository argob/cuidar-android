package ar.gob.coronavirus.flujos.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.gob.coronavirus.data.UserStatus
import ar.gob.coronavirus.data.local.modelo.LocalUser
import ar.gob.coronavirus.flujos.BaseViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificacionRepository
import ar.gob.coronavirus.utils.PreferencesManager
import ar.gob.coronavirus.utils.extensions.applySchedulers
import ar.gob.coronavirus.utils.observables.EventoUnico
import io.reactivex.Single
import io.reactivex.functions.Function3
import java.util.concurrent.TimeUnit

class InicioViewModel(identificacionRepository: IdentificacionRepository) : BaseViewModel() {

    private val _navigationLiveData = MutableLiveData<EventoUnico<NavegacionFragments>>()
    val navigationLiveData: LiveData<EventoUnico<NavegacionFragments>>
        get() = _navigationLiveData

    init {
        if (PreferencesManager.getRefreshToken().isNullOrEmpty() ||
                PreferencesManager.getHash().isNullOrEmpty() ||
                PreferencesManager.getPassword().isNullOrEmpty()) {
            Single.just(NavegacionFragments.LOGIN)
                    .delay(500, TimeUnit.MILLISECONDS) // Used to ensure that the single takes at least half a second
        } else {
            Single.zip(identificacionRepository.user,
                    identificacionRepository.refreshToken(),
                    Single.just(true).delay(500, TimeUnit.MILLISECONDS), // Used to ensure that the single takes at least half a second
                    Function3<LocalUser, Boolean, Boolean, NavegacionFragments> { user, token, _ ->
                        when {
                            user.address?.province.isNullOrEmpty() || !token -> NavegacionFragments.LOGIN
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