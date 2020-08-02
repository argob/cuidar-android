package ar.gob.coronavirus.data.remoto;

import ar.gob.coronavirus.BuildConfig;
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor;
import ar.gob.coronavirus.utils.many.ApiConstants;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CovidRetrofit {
    private static OkHttpClient clienteHttp = null;
    private static Retrofit retrofit = null;
    private static CovidApiService covidApiService = null;

    private HeadersInterceptor headersInterceptor;

    public CovidRetrofit(HeadersInterceptor headersInterceptor) {
        this.headersInterceptor = headersInterceptor;
    }

    private Retrofit obtenerInstanciaRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(obtenerClienteHttp())
                    .build();
        }
        return retrofit;
    }

    private OkHttpClient obtenerClienteHttp() {
        if (clienteHttp == null) {

            OkHttpClient.Builder constructorClienteHttp = new OkHttpClient.Builder();
            constructorClienteHttp.addInterceptor(headersInterceptor);

            String hostname = ApiConstants.CERTIFICATE_MATCHER;
            CertificatePinner certificatePinner = new CertificatePinner.Builder()
                    .add(hostname, BuildConfig.CERTIFICADO_SHA1)
                    .add(hostname, BuildConfig.CERTIFICADO_SHA2)
                    .add(hostname, BuildConfig.CERTIFICADO_SHA3)
                    .build();

            constructorClienteHttp.authenticator(new AppAuthenticator());
            constructorClienteHttp.certificatePinner(certificatePinner);
            if (BuildConfig.DEBUG)
                constructorClienteHttp.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
            clienteHttp = constructorClienteHttp
                    .build();
        }
        return clienteHttp;
    }

    public CovidApiService obtenerCovidApiService() {
        if (covidApiService == null) {
            covidApiService = obtenerInstanciaRetrofit().create(CovidApiService.class);
        }
        return covidApiService;
    }

}
