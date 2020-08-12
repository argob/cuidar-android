package ar.gob.coronavirus.data

class DniEntity(val id: String = "", val procedure: String = "", val gender: String = "") {

    fun hasBasicData(): Boolean {
        return id.isNotEmpty() &&
                procedure.isNotEmpty() &&
                gender.isNotEmpty()
    }

    companion object {
        @JvmStatic
        fun build(valorQrDniEscaneado: String?): DniEntity {
            val splitValues = valorQrDniEscaneado
                    .takeIf { it?.startsWith("@")?.not() ?: false }
                    ?.split("@") ?: return DniEntity()
            return DniEntity(
                    procedure = splitValues.getOrElse(0) { "" },
                    gender = splitValues.getOrElse(3) { "" },
                    id = splitValues.getOrElse(4) { "" }.replace("[a-zA-Z]", ""))
        }
    }
}