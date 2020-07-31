package ar.gob.coronavirus.data

import com.google.gson.annotations.SerializedName

enum class UserStatus(val value: String) {
    @SerializedName("DEBE_AUTODIAGNOSTICARSE")
    MUST_SELF_DIAGNOSE("DEBE_AUTODIAGNOSTICARSE"),

    @SerializedName("NO_INFECTADO")
    NOT_INFECTED("NO_INFECTADO"),

    @SerializedName("NO_CONTAGIOSO")
    NOT_CONTAGIOUS("NO_CONTAGIOSO"),

    @SerializedName("INFECTADO")
    INFECTED("INFECTADO"),

    @SerializedName("DERIVADO_A_SALUD_LOCAL")
    DERIVED_TO_LOCAL_HEALTH("DERIVADO_A_SALUD_LOCAL"),

    // Should never happen
    @SerializedName("UNKNOWN")
    UNKNOWN("UNKNOWN");

    companion object {
        @JvmStatic
        fun fromString(string: String): UserStatus =
                values().find { it.value == string } ?: throw IllegalArgumentException("State $string is unknown")
    }
}