package no.nav.jantra.river.examples.services.basic

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.basic.EventListener
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.publish

class ApplicationStartedListener(rapidsConnection: RapidsConnection) : EventListener(rapidsConnection) {
    override val event: MessageType.Event = EventName.APPLICATION_INITIATED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME.str(), event.value)
    }

    override fun onEvent(packet: Event) {
        rapidsConnection.publish(packet.createBehov(BehovName.FULL_NAME, mapOf(DataFelt.APPLICATION_ID to "123")))
    }
}