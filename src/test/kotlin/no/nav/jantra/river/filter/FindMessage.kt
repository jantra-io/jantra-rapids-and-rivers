package no.nav.helsearbeidsgiver.inntektsmelding.integrasjonstest.filter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains
import no.nav.jantra.river.IDataFelt
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.filter.getBehov

fun findMessage(
    meldinger: List<JsonNode>,
    event: MessageType.Event,
    behovType: MessageType.Behov? = null,
    datafelt: IDataFelt? = null
): List<JsonNode> {
    return meldinger
        .filter { it.get(Key.EVENT_NAME.str).asText() == event.value }
        .filter { behovType == null || it.getBehov().find { it.value == behovType.value }!=null }
        .filter { datafelt == null || (it.contains(Key.DATA.str) && it.contains(datafelt.str)) }
}
