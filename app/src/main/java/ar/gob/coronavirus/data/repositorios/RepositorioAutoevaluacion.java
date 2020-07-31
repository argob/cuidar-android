package ar.gob.coronavirus.data.repositorios;

import ar.gob.coronavirus.data.ConvertirClasesRemotasEnLocales;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.UserDAO;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.data.remoto.modelo.SelfEvaluationResponse;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.AutoevaluacionRemoto;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RepositorioAutoevaluacion implements IRepositorioAutoevaluacion {

    private Api api;
    private UserDAO userDao;

    public RepositorioAutoevaluacion(Api api, UserDAO userDao) {
        this.api = api;
        this.userDao = userDao;
    }

    public Single<LocalUser> confirmarAutoevaluacion(AutoevaluacionRemoto autoevaluacionRemoto) {
        return userDao.select()
                .flatMap(localUser -> Single.fromCallable(() -> {
                    String dni = String.valueOf(localUser.getDni());
                    String sexo = localUser.getGender();

                    SelfEvaluationResponse remoteUser = api.confirmarAutodiagnostico(dni, sexo, autoevaluacionRemoto);
                    if (remoteUser != null) {
                        return ConvertirClasesRemotasEnLocales.updateUser(localUser, remoteUser);
                    }
                    throw new Exception("Error confirmando la autoevaluación");
                }))
                .flatMap(localUser -> Single.fromCallable(() -> {
                    boolean userWasUpdated = userDao.update(localUser) > 0;
                    if (userWasUpdated) {
                        return localUser;
                    }
                    throw new Exception("Error updating local user");
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<UserStatus> obtenerEstadoUsuario() {
        return userDao
                .select()
                .flatMap(localUser -> Single.just(localUser.getCurrentState().getUserStatus()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<LocalUser> obtenerUsuario() {
        return userDao.select()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
