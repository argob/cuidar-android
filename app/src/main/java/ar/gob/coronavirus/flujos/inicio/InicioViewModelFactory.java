package ar.gob.coronavirus.flujos.inicio;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ar.gob.coronavirus.data.local.EncryptedDataBase;
import ar.gob.coronavirus.data.local.UserDAO;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.data.remoto.CovidRetrofit;
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor;
import ar.gob.coronavirus.flujos.identificacion.IdentificacionRepository;
import timber.log.Timber;

public class InicioViewModelFactory implements ViewModelProvider.Factory {

    public InicioViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        HeadersInterceptor headersInterceptor = new HeadersInterceptor();
        CovidRetrofit covidRetrofit = new CovidRetrofit(headersInterceptor);
        Api api = new Api(covidRetrofit);

        // TODO: usar InicioRepository
        UserDAO userDao = EncryptedDataBase.getInstance().getUserDao();
        IdentificacionRepository identificacionRepository = new IdentificacionRepository(api, userDao);

        try {
            return (T) new InicioViewModel(identificacionRepository);
        } catch (Exception e) {
            Timber.e(e);
        }
        throw new IllegalArgumentException("unexpected model class " + modelClass);
    }
}
