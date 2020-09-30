package ar.gob.coronavirus.utils.many

private val VALUES = arrayOf(
        "0", "1", "2", "3",
        "4", "5", "6", "7",
        "8", "9", "A", "B",
        "C", "D", "E", "F"
)

class TokenInfo(private val seed: Int) {

    fun component1() = VALUES[(seed and 0x0F00) / 0x100]

    fun component2() = VALUES[(seed and 0x0F0) / 0x10]

    fun component3() = VALUES[seed and 0x0F]

}