package ar.gob.coronavirus.data.remoto.interceptores;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import ar.gob.coronavirus.BuildConfig;
import ar.gob.coronavirus.GlobalAction;
import ar.gob.coronavirus.GlobalActionsManager;
import ar.gob.coronavirus.data.remoto.AppAuthenticator;
import ar.gob.coronavirus.utils.Constantes;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeadersInterceptor implements Interceptor {

    public static final int CODIGO_DE_ACTUALIZAR_FORZADO = 426;

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("X-App-Platform", "android")
                .addHeader("X-App-Version", "" + BuildConfig.VERSION_CODE)
                .addHeader("Content-Type", "application/json");

        // Don't add authorization header to the authorization request
        String header = AppAuthenticator.createHeader();
        if (!original.url().toString().contains(Constantes.AUTHORIZATION_ENDPOINT) && header != null) {
            requestBuilder.addHeader(Constantes.AUTHORIZATION_HEADER, header);
        }

        Request request = requestBuilder.build();

        Response response = chain.proceed(request);

        if (response.code() == CODIGO_DE_ACTUALIZAR_FORZADO) {
            GlobalActionsManager.INSTANCE.post(GlobalAction.FORCE_UPDATE);
        }

        return response;
    }
}
