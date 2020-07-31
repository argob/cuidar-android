@file:Suppress("unused")

package ar.gob.coronavirus.flujos.autodiagnostico

enum class ProvincesEnum(val stringValue: String) {
    CABA("ciudad autónoma de buenos aires"),
    BSAS("buenos aires");

    companion object {
        @JvmStatic
        fun fromString(s: String?): ProvincesEnum? {
            if (s == null) return null
            for (type in values()) {
                if (type.stringValue.equals(s, ignoreCase = true)) {
                    return type
                }
            }
            return null
        }
    }
}