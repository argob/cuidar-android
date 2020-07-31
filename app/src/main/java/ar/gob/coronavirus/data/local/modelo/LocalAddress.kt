package ar.gob.coronavirus.data.local.modelo

data class LocalAddress(
        val province: String,
        val locality: String,
        val apartment: String?,
        val street: String,
        val number: String,
        val floor: String?,
        val door: String?,
        val postalCode: String,
        val others: String?) {

    override fun toString(): String {
        val nPuerta = if (door == null) "" else "Puerta $door, "
        val nPiso = if (floor == null) "" else "Piso $floor\n"
        val nPuertaPiso = nPuerta + nPiso
        val stringBuilder = StringBuilder()
                .append("Calle ").append(street).append(", ").append("Número ").append(number).append("\n")
                .append(nPuertaPiso)
                .append(locality).append(", ").append(province)
        return stringBuilder.toString()
    }

}