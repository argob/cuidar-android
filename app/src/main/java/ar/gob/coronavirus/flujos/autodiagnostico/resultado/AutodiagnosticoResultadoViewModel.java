package ar.gob.coronavirus.flujos.autodiagnostico.resultado;

import androidx.lifecycle.MutableLiveData;

import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.repositorios.RepositorioAutoevaluacion;
import ar.gob.coronavirus.flujos.BaseViewModel;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class AutodiagnosticoResultadoViewModel extends BaseViewModel {

    private RepositorioAutoevaluacion autoevaluacionRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<LocalUser> usuarioLiveData = new MutableLiveData<>();

    public AutodiagnosticoResultadoViewModel(
            RepositorioAutoevaluacion autoevaluacionRepository) {
        super();
        this.autoevaluacionRepository = autoevaluacionRepository;
    }

    void cargarUsuario() {
        compositeDisposable.add(autoevaluacionRepository
                .obtenerUsuario()
                .subscribe(usuario -> usuarioLiveData.setValue(usuario), throwable ->
                        Timber.e("Error obteniendo el nombre del usuario: %s", throwable.getLocalizedMessage())));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
