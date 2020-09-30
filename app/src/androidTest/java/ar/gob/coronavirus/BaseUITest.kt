package ar.gob.coronavirus

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import ar.gob.coronavirus.data.local.EncryptedDataBase
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.mock.declare

@RunWith(AndroidJUnit4::class)
open class BaseUITest : KoinTest {

    val webServer = MockWebServer()

    @Before
    fun setUp() {
        webServer.start()
        declare(named("base_url")) {
            webServer.url("/").toString()
        }
        declare {
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), EncryptedDataBase::class.java)
                    .build()
                    .userDao
        }
        declare {
            HttpLoggingInterceptor.Level.BASIC
        }
    }

    @After
    fun tearDown() {
        webServer.shutdown()
    }

    fun runFillUserDataFlow() {

        /**
         * Activity
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
         *
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionDniConfirmacionDatosFragment
         */

        Thread.sleep(2000)

        //Click on "next" button
        onView(ViewMatchers.withId(R.id.btn_siguiente_confirmacion_datos_identificacion_fragment)).perform(ViewActions.click())

        /**
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionTelefonoFragment
         */

        //Type the user's telephone and press IME action button
        onView(ViewMatchers.withId(R.id.tie_telefono_identificacion_fragment))
                .perform(ViewActions.typeText("1500000000"))
                .perform(ViewActions.pressImeActionButton())

        //Click on "next" button
        onView(ViewMatchers.withId(R.id.btn_siguiente_telefono_fragment))
                .perform(ViewActions.click())

        /**
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionDireccionCompletaFragment
         */

        //Type the user's province and press IME action button
        onView(ViewMatchers.withId(R.id.dropdown_provincia_identificacion_fragment))
                .perform(ViewActions.typeText("Buenos Aires"))
                .perform(ViewActions.pressImeActionButton())

        //Scroll to locality dropdown, type the user's locality and press IME action button
        onView(ViewMatchers.withId(R.id.dropdown_localidades_identificacion_fragment))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.typeText("Tigre - Tigre"))
                .perform(ViewActions.pressImeActionButton())

        //Scroll to street address editText, type the user's street address and press IME action button
        onView(ViewMatchers.withId(R.id.tie_calle_identificacion_fragment))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.typeText("Calle Falsa"))
                .perform(ViewActions.pressImeActionButton())

        //Scroll to street number editText, type the user's street number and press IME action button
        onView(ViewMatchers.withId(R.id.tie_numero_casa_identificacion_fragment))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.typeText("12345"))
                .perform(ViewActions.pressImeActionButton())

        //Scroll to zip code editText, type the user's zip code and press IME action button
        onView(ViewMatchers.withId(R.id.tie_codigo_postal_identificacion_fragment))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.typeText("1111"))
                .perform(ViewActions.pressImeActionButton())

        //Scroll to "next" button and click it
        onView(ViewMatchers.withId(R.id.btn_siguiente_direccion_completa_identificacion_fragment))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click())

        //Click on "continue" button to accept the results (success or fail)
        onView(ViewMatchers.withId(R.id.btn_action_pantalla_completa_dialog)).perform(ViewActions.click())

        /**
         * Activity
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
         *
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionDniConfirmacionDatosFragment
         */

        /**
         * @see ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.BaseMainFragment
         */

        //Check if PBA button assistance is completely displayed
        onView(ViewMatchers.withId(R.id.pba_button)).check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    }
}