package ar.gob.coronavirus.data.repositorios;

import com.google.firebase.iid.FirebaseInstanceId;

import ar.gob.coronavirus.CovidApplication;
import ar.gob.coronavirus.data.local.EncryptedDataBase;
import ar.gob.coronavirus.data.local.UserDAO;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.PreferencesManager;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RepositorioLogout {

    private Api api;
    private UserDAO userDao;

    public RepositorioLogout(Api api, UserDAO userDao) {
        this.api = api;
        this.userDao = userDao;
    }

    public void logout() {
        eliminarToken();
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (Exception e) {
            Timber.e(e);
        }
        PreferencesManager.INSTANCE.clear();
        EncryptedDataBase.getInstance().getUserDao().deleteAll();
    }

    private void eliminarToken() {
        userDao.select()
                .flatMapCompletable(user -> Completable.fromAction(() -> {
                    String token = CovidApplication.getInstance().getSharedUtils().getString(Constantes.SHARED_KEY_PUSH);
                    CovidApplication.getInstance().getSharedUtils().putString(Constantes.SHARED_KEY_PUSH, "");
                    String dni = String.valueOf(user.getDni());
                    String sexo = user.getGender();

                    api.desregistrarPush(dni, sexo, token);
                }))
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                }, t -> Timber.e(t, "Logout error"));
    }
}

