package no.nav.jantra.river.filter

import com.fasterxml.jackson.databind.JsonNode
import no.nav.jantra.river.InternalBehov
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType

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
