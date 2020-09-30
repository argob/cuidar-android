package ar.gob.coronavirus.flujos.autodiagnostico.resultado;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData;

import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.repositorios.SelfEvaluationRepository;
import ar.gob.coronavirus.flujos.BaseViewModel;
import timber.log.Timber;

class SelfEvaluationResultViewModel(private val selfEvaluationRepository: SelfEvaluationRepository) : BaseViewModel() {

    private val _userLiveData = MutableLiveData<LocalUser>();
    val userLiveData: LiveData<LocalUser>
        get() = _userLiveData

    fun loadUser() {
        selfEvaluationRepository.user
                .subscribe(_userLiveData::setValue
                ) { Timber.e(it, "Error obteniendo usuario") }
                .also { addDisposable(it) }
    }
}
