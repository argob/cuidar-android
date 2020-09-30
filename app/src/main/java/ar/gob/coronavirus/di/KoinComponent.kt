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
import ar.gob.coronavirus.data.repositorios.LogoutRepository
import ar.gob.coronavirus.data.repositorios.SelfEvaluationRepository
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoViewModel
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.SelfEvaluationResultViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificacionViewModel
import ar.gob.coronavirus.flujos.identificacion.IdentificationRepository
import ar.gob.coronavirus.flujos.inicio.SplashViewModel
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalRepository
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalViewModel
import ar.gob.coronavirus.utils.SharedUtils
import ar.gob.coronavirus.utils.many.APIConstants
import com.google.android.gms.location.FusedLocationProviderClient
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
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
    single { EncryptedDataBase.instance.permitsDao }
    single { SharedUtils(androidApplication()) }
}

private const val BASE_URL = "base_url"
private const val STATIC_URL = "static_url"
private const val STATIC = "static"

private val networkModule = module {
    single { if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }
    single(named(BASE_URL)) { APIConstants.BASE_URL }
    single { buildOkHttpClient(true, get()) }
    single { buildRetrofit(get(named(BASE_URL)), get()) }
    single { get<Retrofit>().create<CovidApiService>() }
    single { Api(apiService = get()) }

    single(named(STATIC_URL)) { APIConstants.ADVICE_URL }
    single(named(STATIC)) { buildOkHttpClient(false, get()) }
    single(named(STATIC)) { buildRetrofit(get(named(STATIC_URL)), get()) }
    single { get<Retrofit>(qualifier = named(STATIC)).create<AdviceService>() }
}

private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
    return Retrofit.Builder().run {
        baseUrl(baseUrl)
        client(client)
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        addConverterFactory(GsonConverterFactory.create())
        build()
    }
}

private fun buildOkHttpClient(authenticated: Boolean, logLevel: HttpLoggingInterceptor.Level): OkHttpClient {
    return OkHttpClient.Builder().run {
        if (authenticated) {
            authenticator(AppAuthenticator())
            addInterceptor(AuthenticationInterceptor())
        }
        addInterceptor(HeadersInterceptor())
        addInterceptor(HttpLoggingInterceptor().apply {
            level = logLevel
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
    factory { IdentificationRepository(api = get(), userDao = get(), permitsDao = get()) }
    factory { PantallaPrincipalRepository(api = get(), userDao = get(), adviceService = get(), permitsDao = get()) }
    factory { LogoutRepository(api = get(), userDao = get(), sharedUtils = get(), permitsDao = get()) }
    factory { SelfEvaluationRepository(api = get(), userDao = get()) }
}

private val utilsModule = module {
    factory { FusedLocationProviderClient(androidContext()) }
}

private val viewModelsModule = module {
    viewModel { SplashViewModel(identificationRepository = get(), logoutRepositoryRepository = get()) }
    viewModel { SelfEvaluationResultViewModel(selfEvaluationRepository = get()) }
    // Doesn't support named parameters cause its in java
    viewModel { IdentificacionViewModel(get(), get()) }
    // Doesn't support named parameters cause its in java
    viewModel { AutodiagnosticoViewModel(get(), get(), get(), get()) }
    // Doesn't support named parameters cause its in java
    viewModel { PantallaPrincipalViewModel(get(), get()) }
}