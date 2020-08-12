package ar.gob.coronavirus.data.remoto;

import androidx.annotation.NonNull;

import java.io.IOException;

import ar.gob.coronavirus.data.remoto.modelo.AutorizacionUsuario;
import ar.gob.coronavirus.data.remoto.modelo.PushBody;
import ar.gob.coronavirus.data.remoto.modelo.RemoteAddress;
import ar.gob.coronavirus.data.remoto.modelo.RemoteLocation;
import ar.gob.coronavirus.data.remoto.modelo.RemoteUser;
import ar.gob.coronavirus.data.remoto.modelo.SelfEvaluationResponse;
import ar.gob.coronavirus.data.remoto.modelo.Token;
import ar.gob.coronavirus.data.remoto.modelo.TokenRefreshBody;
import ar.gob.coronavirus.data.remoto.modelo.UserInformationUpdate;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSelfEvaluation;
import ar.gob.coronavirus.utils.PreferencesManager;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

public class Api {

    private CovidApiService apiService;

    public Api(CovidApiService apiService) {
        this.apiService = apiService;
    }

    public RemoteUser getUserInformation(String dni, String sexo) {
        try {
            Response<RemoteUser> usuarioRemoto = apiService
                    .obtenerUsuario(dni, sexo).execute();
            return usuarioRemoto.body();
        } catch (IOException e) {
            return null;
        }
    }

    public RemoteUser updateUser(String dni, String sexo, String telefono, RemoteAddress remoteAddress, RemoteLocation remoteLocation) {
        try {
            Response<RemoteUser> usuarioRemoto = apiService
                    .actualizarUsuario(dni, sexo, new UserInformationUpdate(telefono, remoteAddress, remoteLocation)).execute();
            return usuarioRemoto.body();
        } catch (IOException e) {
            return null;
        }
    }

    public SelfEvaluationResponse confirmarAutodiagnostico(String dni, String sexo, RemoteSelfEvaluation remoteSelfEvaluation) {
        try {
            Response<SelfEvaluationResponse> usuarioRemoto = apiService
                    .confirmarAutoevaluacion(dni, sexo, remoteSelfEvaluation).execute();
            return usuarioRemoto.body();
        } catch (IOException e) {
            return null;
        }
    }

    public Token autorizarUsuario(String dni, String sexo, String nroTramite) {
        try {
            AutorizacionUsuario autorizacionUsuario = new AutorizacionUsuario(dni, sexo, nroTramite);
            Response<Token> usuarioRemoto = apiService
                    .autorizacion(autorizacionUsuario).execute();
            Token body = usuarioRemoto.body();
            if (body == null) {
                return null;
            } else {
                AppAuthenticator.setAccessToken(body.getToken());
                PreferencesManager.INSTANCE.saveRefreshToken(body.getRefreshToken());
                return body;
            }
        } catch (IOException e) {
            Timber.e(e, "Exception authorizing");
            return null;
        }
    }

    public Completable registrarPush(long dni, String sexo, String pushId) {
        return apiService
                .registrarToken(dni, sexo, new PushBody(pushId));
    }

    public boolean desregistrarPush(String dni, String sexo, String pushId) {
        try {
            apiService
                    .desregistrarToken(dni, sexo, new PushBody(pushId)).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @NonNull
    public Single<Token> refresh(String token, String something) {
        return apiService
                .refresh(new TokenRefreshBody(token, something));
    }
}
