package ar.gob.coronavirus.flujos.autodiagnostico;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;

import ar.gob.coronavirus.CovidApplication;
import ar.gob.coronavirus.data.local.EncryptedDataBase;
import ar.gob.coronavirus.data.local.UserDAO;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.data.remoto.CovidRetrofit;
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor;
import ar.gob.coronavirus.data.repositorios.RepositorioAutoevaluacion;
import ar.gob.coronavirus.data.repositorios.RepositorioLogout;
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.AutodiagnosticoResultadoViewModel;
import ar.gob.coronavirus.flujos.identificacion.IdentificacionRepository;


public class AutoevaluacionViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Context context = CovidApplication.getInstance();
        HeadersInterceptor headersInterceptor = new HeadersInterceptor();
        CovidRetrofit covidRetrofit = new CovidRetrofit(headersInterceptor);
        Api api = new Api(covidRetrofit);
        UserDAO userDao = EncryptedDataBase.getInstance().getUserDao();
        RepositorioAutoevaluacion repositorioAutoevaluacion = new RepositorioAutoevaluacion(api, userDao);
        RepositorioLogout repositorioLogout = new RepositorioLogout(api, userDao);
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(context);
        IdentificacionRepository identificacionRepository = new IdentificacionRepository(api, userDao);

        try {
            if (AutodiagnosticoViewModel.class.isAssignableFrom(modelClass)) {
                return (T) new AutodiagnosticoViewModel(repositorioAutoevaluacion, identificacionRepository, repositorioLogout, fusedLocationProviderClient);
            } else if (AutodiagnosticoResultadoViewModel.class.equals(modelClass)) {
                return (T) new AutodiagnosticoResultadoViewModel(repositorioAutoevaluacion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("unexpected model class " + modelClass);
    }
}
