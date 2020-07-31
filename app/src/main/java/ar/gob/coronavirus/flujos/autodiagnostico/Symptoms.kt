package ar.gob.coronavirus.flujos.autodiagnostico

import androidx.annotation.StringRes
import ar.gob.coronavirus.R

enum class Symptoms(val value: String,
                    @StringRes val title: Int,
                    @StringRes val shortDescription: Int) {

    S_PDO("S_PDO", R.string.sintoma_perdida_olfato, R.string.loss_of_smell),
    S_PDG("S_PDG", R.string.sintoma_perdida_gusto, R.string.loss_of_taste),
    S_TOS("S_TOS", R.string.sintoma_tos, R.string.cough),
    S_DDG("S_DDG", R.string.sintoma_dolor_de_garganta, R.string.sore_throat),
    S_DRE("S_DRE", R.string.sintoma_dificultad_respiratoria, R.string.breathing_dificulty);
}