package ar.gob.coronavirus.data.remoto

import ar.gob.coronavirus.GlobalAction
import ar.gob.coronavirus.GlobalActionsManager
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.PreferencesManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

class AppAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val url = response.request.url.toString()
        if (url.contains(Constantes.AUTHORIZATION_ENDPOINT) || url.contains(Constantes.REFRESH_ENDPOINT)) return null

        val api = Api(CovidRetrofit(HeadersInterceptor()))
        val newToken = try {
            api.refresh(PreferencesManager.getRefreshToken(), PreferencesManager.getHash()).blockingGet()
        } catch (e: Exception) {
            Timber.e(e, "Error while refreshing the token")
            GlobalActionsManager.post(GlobalAction.LOGOUT)
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