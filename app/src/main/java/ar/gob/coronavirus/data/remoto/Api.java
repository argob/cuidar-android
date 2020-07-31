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
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.AutoevaluacionRemoto;
import ar.gob.coronavirus.utils.PreferencesManager;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;

public class Api {

    private CovidRetrofit covidRetrofit;

    public Api(CovidRetrofit covidRetrofit) {
        this.covidRetrofit = covidRetrofit;
    }

    public RemoteUser obtenerUsuario(String dni, String sexo) {
        try {
            Response<RemoteUser> usuarioRemoto = covidRetrofit.obtenerCovidApiService()
                    .obtenerUsuario(dni, sexo).execute();
            return usuarioRemoto.body();
        } catch (IOException e) {
            return null;
        }
    }

    public RemoteUser actualizarUsuario(String dni, String sexo, String telefono, RemoteAddress remoteAddress, RemoteLocation remoteLocation) {
        try {
            Response<RemoteUser> usuarioRemoto = covidRetrofit.obtenerCovidApiService()
                    .actualizarUsuario(dni, sexo, new UserInformationUpdate(telefono, remoteAddress, remoteLocation)).execute();
            return usuarioRemoto.body();
        } catch (IOException e) {
            return null;
        }
    }

    public SelfEvaluationResponse confirmarAutodiagnostico(String dni, String sexo, AutoevaluacionRemoto autoevaluacionRemoto) {
        try {
            Response<SelfEvaluationResponse> usuarioRemoto = covidRetrofit.obtenerCovidApiService()
                    .confirmarAutoevaluacion(dni, sexo, autoevaluacionRemoto).execute();
            return usuarioRemoto.body();
        } catch (IOException e) {
            return null;
        }
    }

    public Token autorizarUsuario(String dni, String sexo, String nroTramite) {
        try {
            AutorizacionUsuario autorizacionUsuario = new AutorizacionUsuario(dni, sexo, nroTramite);
            Response<Token> usuarioRemoto = covidRetrofit.obtenerCovidApiService()
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
            return null;
        }
    }

    public Completable registrarPush(long dni, String sexo, String pushId) {
        return covidRetrofit.obtenerCovidApiService()
                .registrarToken(dni, sexo, new PushBody(pushId));
    }

    public boolean desregistrarPush(String dni, String sexo, String pushId) {
        try {
            covidRetrofit.obtenerCovidApiService()
                    .desregistrarToken(dni, sexo, new PushBody(pushId)).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @NonNull
    public Single<Token> refresh(String token, String something) {
        return covidRetrofit.obtenerCovidApiService()
                .refresh(new TokenRefreshBody(token, something));
    }
}
