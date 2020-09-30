package ar.gob.coronavirus.flujos.autodiagnostico

import androidx.annotation.StringRes
import ar.gob.coronavirus.R

enum class Symptoms(val value: String,
                    @StringRes val title: Int,
                    @StringRes val shortDescription: Int) {

    SMELL_LOSS("S_PDO", R.string.sintoma_perdida_olfato, R.string.loss_of_smell),
    TASTE_LOSS("S_PDG", R.string.sintoma_perdida_gusto, R.string.loss_of_taste),
    COUGH("S_TOS", R.string.sintoma_tos, R.string.cough),
    SORE_THROAT("S_DDG", R.string.sintoma_dolor_de_garganta, R.string.sore_throat),
    BREATHING_DIFFICULTY("S_DRE", R.string.sintoma_dificultad_respiratoria, R.string.breathing_dificulty),
    HEADACHE("S_DDC", R.string.headache_question, R.string.headache),
    DIARRHEA("S_DRA", R.string.diarrhea_question, R.string.diarrhea),
    VOMIT("S_VMT", R.string.vomit_question, R.string.vomit),
    MUSCLE_ACHE("S_DMS", R.string.muscle_ache_questions, R.string.muscle_ache);

    companion object {
        @JvmStatic
        fun find(id: String): Symptoms {
            return values().firstOrNull { it.value == id }
                    ?: throw IllegalArgumentException("Invalid symptom $id")
        }
    }
}