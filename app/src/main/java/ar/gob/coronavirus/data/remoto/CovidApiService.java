package ar.gob.coronavirus.data.remoto;

import ar.gob.coronavirus.data.remoto.modelo.AutorizacionUsuario;
import ar.gob.coronavirus.data.remoto.modelo.PushBody;
import ar.gob.coronavirus.data.remoto.modelo.RemoteUser;
import ar.gob.coronavirus.data.remoto.modelo.SelfEvaluationResponse;
import ar.gob.coronavirus.data.remoto.modelo.Token;
import ar.gob.coronavirus.data.remoto.modelo.TokenRefreshBody;
import ar.gob.coronavirus.data.remoto.modelo.UserInformationUpdate;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.AutoevaluacionRemoto;
import ar.gob.coronavirus.utils.Constantes;
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * La autenticación por DNI y nro de trámite es imperfecta pero es la más viable de las
 * opciones disponibles al momento. La situación de emergencia sanitaria impide la concreción de un
 * trámite presencial para el alta.
 *
 * Los mecanismos biométricos suben la barrera de entrada tecnológica además de presentar otros desafíos.
 * Los mecanismos como el de AFIP, si bien más robustos, dejan afuera a un porcentaje alto de la población.
 * Algo similar sucede con los proveedores de identidad extranjeros, sumado al problema que representan
 * para la soberanía digital.
 *
 * En el mediano plazo se requiere de un mecanismo masivo que permita al grueso de la población poder
 * darse de alta en poco tiempo, sin hacer trámites presenciales.
 *
 * A su vez, la identificación de "sexo", que suena tan antagónica hoy en día, es un requisito
 * necesario para poder validar DNIs, que se utiliza en parte para mitigar el problema de los números de
 * DNIs repetidos (herencia de la era pre digital en la asignación de número de DNIs).
 */
interface CovidApiService {

    @POST(Constantes.AUTHORIZATION_ENDPOINT)
    Call<Token> autorizacion(@Body AutorizacionUsuario autorizacionUsuario);

    @POST(Constantes.REFRESH_ENDPOINT)
    Single<Token> refresh(@Body TokenRefreshBody body);

    @GET("usuarios/{dni}")
    Call<RemoteUser> obtenerUsuario(@Path("dni") String dni, @Query("sexo") String sexo);

    @PATCH("usuarios/{dni}")
    Call<RemoteUser> actualizarUsuario(
            @Path("dni") String dni,
            @Query("sexo") String sexo,
            @Body UserInformationUpdate userInformationUpdate);

    @POST("usuarios/{dni}/notificaciones/registrar")
    Completable registrarToken(@Path("dni") long dni, @Query("sexo") String sexo, @Body PushBody pushBody);

    @POST("usuarios/{dni}/notificaciones/desregistrar")
    Call<ResponseBody> desregistrarToken(@Path("dni") String dni, @Query("sexo") String sexo, @Body PushBody pushBody);

    @POST("/usuarios/{dni}/autoevaluaciones")
    Call<SelfEvaluationResponse> confirmarAutoevaluacion(
            @Path("dni") String dni,
            @Query("sexo") String sexo,
            @Body AutoevaluacionRemoto autoevaluacionRemoto
    );
}
