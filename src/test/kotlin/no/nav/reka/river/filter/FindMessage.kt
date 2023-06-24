package no.nav.helsearbeidsgiver.inntektsmelding.integrasjonstest.filter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains
import no.nav.petrov.river.Key
import no.nav.petrov.river.MessageType
import no.nav.petrov.river.filter.getBehov

fun findMessage(
    meldinger: List<JsonNode>,
    event: MessageType.Event,
    behovType: MessageType.Behov? = null,
    datafelt: MessageType.Data? = null
): List<JsonNode> {
    return meldinger
        .filter { it.get(Key.EVENT_NAME.str).asText() == event.name }
        .filter { behovType == null || it.getBehov().contains(behovType) }
        .filter { datafelt == null || (it.contains(Key.DATA.str) && it.contains(datafelt.name)) }
}
