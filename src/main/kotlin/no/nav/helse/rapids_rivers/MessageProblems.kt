package no.nav.helse.rapids_rivers

class MessageProblems(private val originalMessage: String) {
    private val severe = mutableListOf<String>()

    fun severe(melding: String, vararg params: Any): Nothing {
        severe.add(String.format(melding, *params))
        throw MessageException(this)
    }

    fun hasErrors() = severe.isNotEmpty()

    override fun toString(): String {
        return (severe.map { "S: $it" })
            .joinToString(separator = "\n")
    }


    class MessageException(val problems: MessageProblems) : RuntimeException(problems.toString())
}
