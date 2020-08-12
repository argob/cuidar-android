package ar.gob.coronavirus.data.remoto.interceptores

import ar.gob.coronavirus.data.remoto.AppAuthenticator
import ar.gob.coronavirus.utils.Constantes
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val header = AppAuthenticator.createHeader()
        // Don't add authorization header to the authorization request
        if (!original.url.toString().contains(Constantes.AUTHORIZATION_ENDPOINT) && header != null) {
            return original.newBuilder().run {
                addHeader(Constantes.AUTHORIZATION_HEADER, header)
                chain.proceed(build())
            }
        }
        return chain.proceed(original)
    }

}