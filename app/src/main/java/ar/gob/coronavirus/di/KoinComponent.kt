package ar.gob.coronavirus.di

import android.app.Application
import ar.gob.coronavirus.BuildConfig
import ar.gob.coronavirus.data.local.EncryptedDataBase
import ar.gob.coronavirus.data.remoto.AdviceService
import ar.gob.coronavirus.data.remoto.Api
import ar.gob.coronavirus.data.remoto.AppAuthenticator
import ar.gob.coronavirus.data.remoto.CovidApiService
import ar.gob.coronavirus.data.remoto.interceptores.AuthenticationInterceptor
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor
import ar.gob.coronavirus.data.repositorios.RepositorioAutoevaluacion
import ar.gob.coronavirus.data.repositorios.RepositorioLogout
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoViewModel
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.AutodiagnosticoResultadoViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificacionViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificationRepository
import ar.gob.coronavirus.flujos.inicio.SplashViewModel
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalRepository
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel
import ar.gob.coronavirus.utils.many.APIConstants
import com.google.android.gms.location.FusedLocationProviderClient
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

fun startKoin(application: Application) {
    startKoin {
        androidContext(application)
        modules(databaseModule, networkModule, utilsModule, repositoriesModule, viewModelsModule)
    }
}

private val databaseModule = module {
    single { EncryptedDataBase.instance.userDao }
}

private val networkModule = module {
    single { buildRetrofit(APIConstants.BASE_URL) }
    single { get<Retrofit>().create<CovidApiService>() }
    single { Api(get()) }

    single(named("static")) { buildRetrofit(APIConstants.ADVICE_URL, false) }
    single { get<Retrofit>(qualifier = named("static")).create<AdviceService>() }
}

private fun buildRetrofit(baseUrl: String, authenticated: Boolean = true): Retrofit {
    return Retrofit.Builder().run {
        baseUrl(baseUrl)
        client(buildOkHttpClient(authenticated))
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        addConverterFactory(GsonConverterFactory.create())
        build()
    }
}

private fun buildOkHttpClient(authenticated: Boolean): OkHttpClient {
    return OkHttpClient.Builder().run {
        if (authenticated) {
            authenticator(AppAuthenticator())
            addInterceptor(AuthenticationInterceptor())
        }
        addInterceptor(HeadersInterceptor())
        addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        })
        certificatePinner(CertificatePinner.Builder().run {
            add(APIConstants.CERTIFICATE_MATCHER, BuildConfig.CERTIFICADO_SHA1)
            add(APIConstants.CERTIFICATE_MATCHER, BuildConfig.CERTIFICADO_SHA2)
            add(APIConstants.CERTIFICATE_MATCHER, BuildConfig.CERTIFICADO_SHA3)
            build()
        })
        build()
    }
}

private val repositoriesModule = module {
    factory { IdentificationRepository(api = get(), userDao = get()) }
    factory { PantallaPrincipalRepository(api = get(), userDao = get(), adviceService = get()) }
    // Doesn't support named parameters cause its in java
    factory { RepositorioLogout(get(), get()) }
    // Doesn't support named parameters cause its in java
    factory { RepositorioAutoevaluacion(get(), get()) }
}

private val utilsModule = module {
    factory { FusedLocationProviderClient(androidContext()) }
}

private val viewModelsModule = module {
    viewModel { SplashViewModel(identificationRepository = get(), logoutRepository = get()) }
    // Doesn't support named parameters cause its in java
    viewModel { IdentificacionViewModel(get(), androidContext().resources, get()) }
    // Doesn't support named parameters cause its in java
    viewModel { AutodiagnosticoViewModel(get(), get(), get(), get()) }
    // Doesn't support named parameters cause its in java
    viewModel { AutodiagnosticoResultadoViewModel(get()) }
    // Doesn't support named parameters cause its in java
    viewModel { PantallaPrincipalViewModel(get(), get()) }
}