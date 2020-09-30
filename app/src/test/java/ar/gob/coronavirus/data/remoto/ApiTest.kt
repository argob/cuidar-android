package ar.gob.coronavirus.data.remoto

import ar.gob.coronavirus.data.remoto.modelo.TokenRefreshBody
import ar.gob.coronavirus.di.startKoin
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.PreferencesManager
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declare

class ApiTest : KoinTest {
    private val webServer = MockWebServer()
    private val api by inject<Api>()

    @Before
    fun setUp() {
        webServer.start()
        startKoin(mockk())
        declare(named("base_url")) {
            webServer.url("/").toString()
        }
        declare {
            HttpLoggingInterceptor.Level.NONE
        }
        mockkObject(PreferencesManager)
        every { PreferencesManager.getHash() } returns "hash"
        every { PreferencesManager.getRefreshToken() } returns "refresh_token"
        AppAuthenticator.setAccessToken("old_token")
    }

    @After
    fun tearDown() {
        unmockkObject(PreferencesManager)
        stopKoin()
        webServer.shutdown()
    }

    @Test
    fun testTokenRefresh() {
        // First call, obtain the user info, should return 401 to trigger the refresh
        webServer.enqueue(MockResponse().setResponseCode(401))
        // Second call, refresh token endpoint, should return a 200 AND a new token
        webServer.enqueue(MockResponse().setResponseCode(200).setBody("{\"token\": \"new_token\", \"refresh_token\": \"refresh_token\"}"))
        // Third call, obtain the user info with the new token, returns 200 and user info
        webServer.enqueue(MockResponse().setResponseCode(200).setBody("{\"dni\":55555555,\"sexo\":\"F\",\"nombres\":\"Nombre_5\",\"apellidos\":\"Apellido_5\",\"telefono\":\"+5455555555\",\"domicilio\":{\"localidad\":\"BARRIO DE LOS PESCADORES\",\"provincia\":\"Chaco\",\"departamento\":\"1° de Mayo\",\"calle\":\"faksa\",\"numero\":\"123\",\"codigo-postal\":\"1111\"},\"fecha-nacimiento\":\"1905-05-05\",\"estado-actual\":{\"nombre-estado\":\"DERIVADO_A_SALUD_LOCAL\",\"fecha-hora-vencimiento\":\"2020-08-16T11:22:11.511-03:00\",\"datos-coep\":{\"coep\":\"La Rioja\",\"informacionDeContacto\":\"107 o 911\"}}}"))

        val remoteUser = api.getUserInformation("12341234", "M").blockingGet()
        assertEquals("Nombre_5", remoteUser.names)

        // Verify requests received by server in queue
        val originalRequest = webServer.takeRequest()
        assertEquals("Basic old_token", originalRequest.headers[Constantes.AUTHORIZATION_HEADER])
        val refreshTokenRequest = webServer.takeRequest()
        val result = refreshTokenRequest.body.fromJson<TokenRefreshBody>()
        assertEquals(TokenRefreshBody("refresh_token", "hash"), result)
        val newRequest = webServer.takeRequest()
        assertEquals("Basic new_token", newRequest.headers[Constantes.AUTHORIZATION_HEADER])
    }

    private inline fun <reified T> Buffer.fromJson(): T = Gson().fromJson(readString(Charsets.UTF_8), T::class.java)
}