package ar.gob.coronavirus.data.remoto

import ar.gob.coronavirus.GlobalAction
import ar.gob.coronavirus.GlobalActionsManager
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.PreferencesManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.UnknownHostException

class AppAuthenticator : Authenticator, KoinComponent {
    // Can't be injected by constructor because it would cause circular dependencies
    private val api by inject<Api>()

    /**
     * Si devolvemos null retrofit va a tirar una excepción que hay que handlear donde correspoda
     * Si devolvemos un request retrofit va a intentar ejecutarlo.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        val url = response.request.url.toString()
        //si el 401 es en el AUTHORIZATION_ENDPOINT lo catchea la propia activity de login
        //si el 401 es en el REFRESH_ENDPOINT lo catchea el try-catch de mas abajo
        //si el 401 es registrando/desregistrando las notificaciones no hacemos nada
        if (url.contains(Constantes.AUTHORIZATION_ENDPOINT) || url.contains(Constantes.REFRESH_ENDPOINT) || url.contains(Constantes.NOTIFICATIONS)) return null

        val newToken = try {
            api.refresh(PreferencesManager.getRefreshToken()!!, PreferencesManager.getHash()!!).blockingGet()
        } catch (e: Exception) {
            Timber.e(e, "Error while refreshing the token")
            when {
                e is HttpException && e.code() == HTTP_UNAUTHORIZED -> GlobalActionsManager.post(GlobalAction.INVALID_REFRESH_TOKEN)
                e is UnknownHostException -> GlobalActionsManager.post(GlobalAction.NO_INTERNET_CONNECTION)
                // Cualquier error que se de al hacer refresh (que no sea los casos de arriba) genera un logout
                else -> GlobalActionsManager.post(GlobalAction.LOGOUT)
            }
            return null
        }

        accessToken = newToken.token

        return response.request.newBuilder().run {
            val header = createHeader() ?: return null
            header(Constantes.AUTHORIZATION_HEADER, header)
            build()
        }
    }

    companion object {
        @JvmStatic
        private var accessToken: String? = null

        @JvmStatic
        fun setAccessToken(token: String) = run { accessToken = token }

        @JvmStatic
        fun createHeader() = accessToken?.run { "Basic $accessToken" }
    }
}