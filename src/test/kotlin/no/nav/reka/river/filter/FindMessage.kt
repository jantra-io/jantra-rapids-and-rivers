package no.nav.helsearbeidsgiver.inntektsmelding.integrasjonstest.filter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.filter.getBehov

fun findMessage(
    meldinger: List<JsonNode>,
    event: MessageType.Event,
    behovType: MessageType.Behov? = null,
    datafelt: IDataFelt? = null
): List<JsonNode> {
    return meldinger
        .filter { it.get(Key.EVENT_NAME.str).asText() == event.name }
        .filter { behovType == null || it.getBehov().contains(behovType) }
        .filter { datafelt == null || (it.contains(Key.DATA.str) && it.contains(datafelt.str)) }
}
