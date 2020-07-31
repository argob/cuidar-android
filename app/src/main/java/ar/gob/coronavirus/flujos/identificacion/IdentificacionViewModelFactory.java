package ar.gob.coronavirus.flujos.identificacion;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ar.gob.coronavirus.data.local.EncryptedDataBase;
import ar.gob.coronavirus.data.local.UserDAO;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.data.remoto.CovidRetrofit;
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor;
import ar.gob.coronavirus.data.repositorios.RepositorioLogout;
import ar.gob.coronavirus.flujos.BaseActivity;
import timber.log.Timber;

public class IdentificacionViewModelFactory implements ViewModelProvider.Factory {

    IdentificacionNavegador identificacionNavegador;
    private BaseActivity context;

    public IdentificacionViewModelFactory(BaseActivity context, IdentificacionNavegador identificacionNavegador) {
        this.context = context;
        this.identificacionNavegador = identificacionNavegador;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        HeadersInterceptor headersInterceptor = new HeadersInterceptor();
        CovidRetrofit covidRetrofit = new CovidRetrofit(headersInterceptor);
        Api api = new Api(covidRetrofit);
        Resources resources = context.getResources();
        UserDAO userDao = EncryptedDataBase.getInstance().getUserDao();
        IdentificacionRepository identificacionRepository = new IdentificacionRepository(api, userDao);

        RepositorioLogout repositorioLogout = new RepositorioLogout(api, userDao);

        try {
            return (T) new IdentificacionViewModel(identificacionRepository, resources, repositorioLogout, identificacionNavegador);
        } catch (Exception e) {
            Timber.e(e);
        }
        throw new IllegalArgumentException("unexpected model class " + modelClass);
    }
}
