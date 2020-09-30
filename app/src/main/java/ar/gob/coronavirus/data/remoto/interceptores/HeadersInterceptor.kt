package ar.gob.coronavirus.data.remoto.interceptores

import ar.gob.coronavirus.BuildConfig
import ar.gob.coronavirus.GlobalAction
import ar.gob.coronavirus.GlobalActionsManager
import ar.gob.coronavirus.utils.Constantes
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException

private const val FORCE_UPDATE_CODE = 426

class HeadersInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
                .addHeader("X-App-Platform", "android")
                .addHeader("X-App-Version", "" + BuildConfig.VERSION_CODE)
                .addHeader("Content-Type", "application/json")
                .addHeader("CovFF-MultipleCUCHs", "true")

        // Don't add authorization header to the authorization request
        val request = requestBuilder.build()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e)
            // No queremos mostrar errores de internet por un proceso de background (registrar token)
            if (e is UnknownHostException && !request.url.toString().contains(Constantes.NOTIFICATIONS)) {
                GlobalActionsManager.post(GlobalAction.NO_INTERNET_CONNECTION)
            }
            // Throw the exception to be handled by caller
            throw e
        }

        if (response.code == FORCE_UPDATE_CODE) {
            GlobalActionsManager.post(GlobalAction.FORCE_UPDATE)
        }
        return response
    }
}