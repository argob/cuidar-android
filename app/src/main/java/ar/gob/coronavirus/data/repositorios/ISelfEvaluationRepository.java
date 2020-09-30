package ar.gob.coronavirus.data.repositorios;

import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import io.reactivex.Single;

public interface ISelfEvaluationRepository {

    Single<UserStatus> getUserStatus();

    Single<LocalUser> getUser();
}
