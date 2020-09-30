package ar.gob.coronavirus

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import ar.gob.coronavirus.data.UserStatus
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
import ar.gob.coronavirus.utils.FileReader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Rule
import org.junit.Test

class HappyPathTest : BaseUITest() {

    private val USER_DNI = "12341234"
    private val USER_TRAMIT_NUMBER = "000"

    @Rule
    @JvmField
    val identificacionActivityTestRule: ActivityTestRule<IdentificacionActivity> = ActivityTestRule(IdentificacionActivity::class.java, true, false)

    @Test
    fun happy_path_user_not_infected() {
        webServer.dispatcher = createDispatcher(UserStatus.NOT_INFECTED)
        performLogin()
        runAutodiagnostic()

        /**
         * @see ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.BaseMainFragment
         */

        onView(withId(R.id.sintoma_resultado)).check(matches(withText(R.string.sintomas_resultado_sin_sintomas)))
    }

    @Test
    fun happy_path_user_infected() {
        webServer.dispatcher = createDispatcher(UserStatus.INFECTED)
        performLogin()
        runAutodiagnostic()

        /**
         * @see ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.BaseMainFragment
         */

        onView(withId(R.id.sintoma_resultado)).check(matches(withText(R.string.derivado_de_salud_covid_positivo)))
    }

    @Test
    fun update_user_data_with_pba_locality_should_show_pba_button_assistance() {
        webServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when {
                    request.path.equals("/autorizacion_v3") && request.method.equals("POST") ->
                        return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("autorizacion_v3_response.json"))
                    request.path.equals("/usuarios/$USER_DNI/autoevaluaciones?sexo=M") && request.method.equals("POST") ->
                        return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("autodiagnostic_response_NO_INFECTADO.json"))
                    request.path.equals("/usuarios/$USER_DNI?sexo=M") && request.method.equals("GET") ->
                        return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("get_user_update_data_required.json"))
                    request.path.equals("/usuarios/$USER_DNI?sexo=M") && request.method.equals("PATCH") ->
                        return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("patch_user_update_data_pba_locality.json"))
                }

                return MockResponse().setResponseCode(404)
            }
        }

        performLogin()
        runFillUserDataFlow()
    }

    private fun createDispatcher(userStatus: UserStatus): Dispatcher {
        return object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/autorizacion_v3" -> return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("autorizacion_v3_response.json"))
                    "/usuarios/$USER_DNI?sexo=M" -> return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("get_user_autodiagnostic_needed_response.json"))
                    "/usuarios/$USER_DNI/autoevaluaciones?sexo=M" -> return MockResponse().setResponseCode(200).setBody(FileReader.readStringFromFile("autodiagnostic_response_${userStatus.value}.json"))
                }

                return MockResponse().setResponseCode(404)
            }
        }
    }

    private fun performLogin() {
        identificacionActivityTestRule.launchActivity(null)

        /**
         * Activity
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
         *
         * Fragment
         * @see ar.gob.coronavirus.flujos.identificacion.IdentificacionDniManualFragment
         */

        //Type the user's DNI
        onView(withId(R.id.et_numero_dni_identificacion_fragment))
                .perform(typeText(USER_DNI))

        //Type the user's Tramit Number
        onView(withId(R.id.et_numero_tramite_identificacion_fragment))
                .perform(typeText(USER_TRAMIT_NUMBER))

        //Type the user's gender
        onView(withId(R.id.rb_masculino_identificacion_fragment))
                .perform(scrollTo())
                .perform(click())

        //Click on button to do the login
        onView(withId(R.id.btn_siguiente_dni_manual_identificacion_fragment))
                .perform(scrollTo())
                .perform(click())
    }

    private fun runAutodiagnostic() {

        /**
         * Activity
         * @see ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity
         *
         * @see ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoTemperaturaFragment
         */
        
        Thread.sleep(2000)

        //Scroll down and click on "next" button
        onView(withId(R.id.nestedScrollView)).perform(swipeUp())
        onView(withId(R.id.btn_siguiente)).perform(click())

        /**
         * @see ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoSintomasFragment
         */

        //Scroll down and click on "next" button

        //The double call to "perform(swipeUp())" is a workaround to get to the bottom of the list
        //if the list is too long this could not work.
        onView(withId(R.id.nestedScrollView)).perform(swipeUp()).perform(swipeUp())
        onView(withId(R.id.btn_siguiente)).perform(click())

        /**
         * @see ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoAntecedentesFragment
         */

        //Scroll down and click on "next" button
        onView(withId(R.id.nestedScrollView)).perform(swipeUp())
        onView(withId(R.id.btn_siguiente)).perform(click())

        /**
         * @see ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoConfirmacionFragment
         */

        //Scroll down and click on "next" button
        onView(withId(R.id.nestedScrollView)).perform(swipeUp())
        onView(withId(R.id.btn_siguiente)).perform(click())

        //Click on the dialog's button with text "R.string.enviar"
        onView(withText(R.string.enviar)).perform(click())

        /**
         * Activity
         * @see ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity
         */

        onView(withId(R.id.btnAceptarResultado)).perform(click())
    }
}