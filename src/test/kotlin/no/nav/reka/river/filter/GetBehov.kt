package no.nav.reka.river.filter

import com.fasterxml.jackson.databind.JsonNode
import no.nav.petrov.river.InternalBehov
import no.nav.petrov.river.Key
import no.nav.petrov.river.MessageType

fun JsonNode.getBehov(): List<MessageType.Behov> {
    val node = this.get(Key.BEHOV.str)
    if (this.get(Key.BEHOV.str) == null) {
        return emptyList()
    }
    if (node.isArray) {
        return node.toMutableList().map { InternalBehov(it.textValue()) }
    }
    if (this.get(Key.BEHOV.str).asText().isBlank()) {
        return emptyList()
    }
    return listOf(InternalBehov(node.asText()))
}
