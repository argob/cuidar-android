package ar.gob.coronavirus.flujos;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends ViewModel {
    CompositeDisposable disposable = new CompositeDisposable();

    protected MutableLiveData<Boolean> resultadoDialogoDePermisoDeUbicacionLivaData = new MutableLiveData<>();
    protected MutableLiveData<Integer> lanzarDialogoPermisosLocalizacionLiveData = new MutableLiveData<>();

    public BaseViewModel() {
    }

    public MutableLiveData<Boolean> obtenerResultadoDialogoDePermisoDeUbicacionLivaData() {
        return resultadoDialogoDePermisoDeUbicacionLivaData;
    }

    public MutableLiveData<Integer> obtenerLanzarDialogoPermisosLocalizacionLiveData() {
        return lanzarDialogoPermisosLocalizacionLiveData;
    }

    public void setResultadoDialogoCustomPermisoDeUbicacion(boolean puedeMostrarDialogoNativo) {
        resultadoDialogoDePermisoDeUbicacionLivaData.setValue(puedeMostrarDialogoNativo);
    }

    public void lanzarDialogoPermisosLocalizacion(int tipoDePermisoDeUbicacion) {
        lanzarDialogoPermisosLocalizacionLiveData.setValue(tipoDePermisoDeUbicacion);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }

    protected void addDisposable(Disposable disposable) {
        this.disposable.add(disposable);
    }
}
