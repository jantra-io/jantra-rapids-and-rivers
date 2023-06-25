package no.nav.reka.river.examples.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.EventListener
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.model.Event

class ApplicationStartedListener(rapidsConnection: RapidsConnection) : EventListener(rapidsConnection) {
    override val event: MessageType.Event = EventName.APPLICATION_INITIATED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME.str(), event.value)
    }

    override fun onEvent(packet: Event) {
        publishBehov(packet.createBehov(BehovName.FULL_NAME, mapOf(DataFelt.APPLICATION_ID to "123")))
    }
}