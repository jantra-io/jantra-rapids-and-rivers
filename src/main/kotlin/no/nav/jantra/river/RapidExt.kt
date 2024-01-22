package no.nav.jantra.river

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.model.Data
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.model.Fail
import no.nav.jantra.river.model.Message


fun RapidsConnection.publish(behov: Behov) {
    this.publish(behov.riverId(), behov.toJsonMessage().toJson())
}

fun RapidsConnection.publish(data: Data) {
    this.publish(data.riverId(),data.toJsonMessage().toJson())
}
fun RapidsConnection.publish(event: Event) {
    this.publish(event.toJsonMessage().toJson())
}

fun RapidsConnection.publish(fail: Fail) {
    this.publish(fail.riverId(), fail.toJsonMessage().toJson())
}

fun RapidsConnection.publish(message: Message) {
    this.publish(message.toJsonMessage().toJson())
}


operator fun River.PacketValidation.plus(validation: River.PacketValidation) : River.PacketValidation {
    return River.PacketValidation {
        this.validate(it)
        validation.validate(it)
    }
}
