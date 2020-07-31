package ar.gob.coronavirus.data.repositorios;

import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import io.reactivex.Single;

public interface IRepositorioAutoevaluacion {

    Single<UserStatus> obtenerEstadoUsuario();

    Single<LocalUser> obtenerUsuario();
}
