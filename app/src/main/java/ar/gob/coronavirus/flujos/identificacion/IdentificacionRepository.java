package ar.gob.coronavirus.flujos.identificacion;

import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales;
import ar.gob.coronavirus.data.LocalToRemoteMapper;
import ar.gob.coronavirus.data.local.UserDAO;
import ar.gob.coronavirus.data.local.modelo.LocalAddress;
import ar.gob.coronavirus.data.local.modelo.LocalLocation;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.data.remoto.modelo.RemoteAddress;
import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation;
import ar.gob.coronavirus.data.remoto.modelo.RemoteUser;
import ar.gob.coronavirus.data.remoto.modelo.Token;
import ar.gob.coronavirus.utils.PreferencesManager;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class IdentificacionRepository {

    private Api api;
    private UserDAO userDao;

    public IdentificacionRepository(Api api, UserDAO userDao) {
        this.api = api;
        this.userDao = userDao;
    }

    public boolean autorizarUsuario(String dni, String sexo, String nroTramite) {
        Token token = api.autorizarUsuario(dni, sexo, nroTramite);
        return token != null && !token.getRefreshToken().isEmpty();
    }

    public Single<LocalUser> obtenerUsuario() {
        return userDao.select()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<LocalUser> getUser() {
        return userDao.select()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<LocalUser> registrarUsuario(String dni, String sexo) {
        return Single
                .fromCallable(() -> api.obtenerUsuario(dni, sexo))
                .flatMap(remoteUser -> Single.fromCallable(() -> ConvertirClasesRemotasEnLocales.convertirUsuario(remoteUser)))
                .flatMap(localUser -> Single.fromCallable(() -> {
                    boolean userWasInserted = userDao.insert(localUser) > -1;
                    if (userWasInserted) {
                        return localUser;
                    }
                    throw new Exception("Error inserting local user");
                }))
                .doOnError(Timber::e)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable actualizarUsuario(String dni, String sexo, String telefono, LocalAddress localAddress, LocalLocation localLocation) {
        return Single.fromCallable(() -> {
            RemoteAddress address = LocalToRemoteMapper.mapLocalToRemoteAddress(localAddress);
            RemoteLocation location = LocalToRemoteMapper.mapLocalToRemoteLocation(localLocation);
            RemoteUser remoteUser = api.actualizarUsuario(dni, sexo, telefono, address, location);
            return ConvertirClasesRemotasEnLocales.convertirUsuario(remoteUser);
        }).flatMapCompletable(localUser -> Completable.fromCallable(() -> {
            boolean userWasUpdated = userDao.update(localUser) > 0;
            if (userWasUpdated) {
                return localUser;
            }
            throw new Exception("Error updating local user");
        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> refreshToken() {
        return api.refresh(PreferencesManager.INSTANCE.getRefreshToken(), PreferencesManager.INSTANCE.getHash())
                .flatMap(t -> Single.just(true))
                .onErrorReturnItem(false);
    }

}
