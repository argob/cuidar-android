package ar.gob.coronavirus.flujos.autodiagnostico

import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteAntecedents
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSymptom
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AutodiagnosticoViewModelTest {

    private val viewModel = AutodiagnosticoViewModel(mockk(), mockk(), mockk(), mockk())

    @Before
    fun setUp() {
        // To start the test add all symptoms as false
        Symptoms.values().forEach {
            viewModel.agregarSintoma(RemoteSymptom(it.value, "", false))
        }
    }

    @Test
    fun testShouldRequestLocation_withAnyTwoSymptoms_shouldReturnTrue() {
        // Since its random we run it multiple times
        val randomSymptoms = Symptoms.values().toList()
                .shuffled()
                .take(2)
        randomSymptoms.forEach { viewModel.agregarSintoma(RemoteSymptom(it.value, "", true)) }

        assertTrue("Failed for values $randomSymptoms", viewModel.debePedirPermisoDeLocalizacion())
    }

    @Test
    fun testShouldRequestLocation_withAnySymptomAndTemperature_shouldReturnTrue() {
        val randomSymptom = Symptoms.values().toList().random()
        viewModel.agregarSintoma(RemoteSymptom(randomSymptom.value, "", true))
        viewModel.temperatura = 37.5

        assertTrue("Failed for value $randomSymptom", viewModel.debePedirPermisoDeLocalizacion())
    }

    @Test
    fun testShouldRequestLocation_withOneSymptomAndTightContact_shouldReturnTrue() {
        val validSymptoms = Symptoms.values().toList().toMutableList().apply {
            remove(Symptoms.HEADACHE)
            remove(Symptoms.DIARRHEA)
            remove(Symptoms.VOMIT)
        }
        val randomSymptom = validSymptoms.random()
        viewModel.agregarSintoma(RemoteSymptom(randomSymptom.value, "", true))
        viewModel.agregarAntecedente(RemoteAntecedents(Antecedents.A_CE1.id, "", true))

        assertTrue("Failed for value $randomSymptom", viewModel.debePedirPermisoDeLocalizacion())
    }

    @Test
    fun testShouldRequestLocation_withOneSymptomAndSporadicContact_shouldReturnTrue() {
        val validSymptoms = Symptoms.values().toList().toMutableList().apply {
            remove(Symptoms.HEADACHE)
            remove(Symptoms.DIARRHEA)
            remove(Symptoms.VOMIT)
        }
        val randomSymptom = validSymptoms.random()
        viewModel.agregarSintoma(RemoteSymptom(randomSymptom.value, "", true))
        viewModel.agregarAntecedente(RemoteAntecedents(Antecedents.A_CE2.id, "", true))

        assertTrue("Failed for value $randomSymptom", viewModel.debePedirPermisoDeLocalizacion())
    }

    @Test
    fun testShouldRequestLocation_withOnlyOneSymptom_shouldReturnFalse() {
        val validSymptoms = Symptoms.values().toMutableList().apply {
            // Any of the following two by themselves would be positive
            remove(Symptoms.TASTE_LOSS)
            remove(Symptoms.SMELL_LOSS)
        }
        val randomSymptom = validSymptoms.random()
        viewModel.agregarSintoma(RemoteSymptom(randomSymptom.value, "", true))

        assertFalse("Failed for value $randomSymptom", viewModel.debePedirPermisoDeLocalizacion())
    }
}