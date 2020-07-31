package ar.gob.coronavirus.flujos.autodiagnostico

import androidx.annotation.StringRes
import ar.gob.coronavirus.R

enum class Antecedents(val id: String, @StringRes val title: Int, @StringRes val shortText: Int = title) {
    A_CE1("A_CE1", R.string.antecedentes_contacto_estrecho_1, R.string.antecedentes_contacto_estrecho_1_short),
    A_CE2("A_CE2", R.string.antecedentes_contacto_estrecho_2, R.string.antecedentes_contacto_estrecho_2_short),
    A_EMB("A_EMB", R.string.antecedentes_embarazo),
    A_CAN("A_CAN", R.string.antecedentes_cancer),
    A_DIA("A_DIA", R.string.antecedentes_diabetes),
    A_HEP("A_HEP", R.string.antecedentes_enfermedad_hepatica),
    A_REN("A_REN", R.string.antecedentes_enfermedad_renal),
    A_RES("A_RES", R.string.antecedentes_enfermedad_respiratoria),
    A_CAR("A_CAR", R.string.antecedentes_enfermedad_cardiaca),
    A_BD("A_BD", R.string.antecedentes_defensas_bajas, R.string.antecedentes_defensas_bajas_short);
}