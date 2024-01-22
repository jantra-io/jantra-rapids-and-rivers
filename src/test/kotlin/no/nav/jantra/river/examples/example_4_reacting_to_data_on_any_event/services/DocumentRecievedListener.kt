package no.nav.jantra.river.examples.example_4_reacting_to_data_on_any_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.*
import no.nav.jantra.river.basic.EventListener
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Event

class DocumentRecievedListener(rapidsConnection: RapidsConnection, ) : EventListener(rapidsConnection) {
    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME,event)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onEvent(packet: Event) {
        rapidsConnection.publish(packet.createBehov(BehovName.FORMAT_JSON, mapOf(DataFelt.RAW_DOCUMENT to packet[DataFelt.RAW_DOCUMENT])))
        rapidsConnection.publish(packet.createBehov(BehovName.FORMAT_XML, mapOf(DataFelt.RAW_DOCUMENT to packet[DataFelt.RAW_DOCUMENT])))
    }

}