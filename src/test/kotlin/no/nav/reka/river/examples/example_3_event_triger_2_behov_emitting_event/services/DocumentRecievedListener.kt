package no.nav.reka.river.examples.example_3_event_triger_2_behov_emitting_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.*
import no.nav.reka.river.basic.EventListener
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.model.Event

class DocumentRecievedListener(rapidsConnection: RapidsConnection, ) : EventListener(rapidsConnection) {
    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onEvent(packet: Event) {
         rapidsConnection.publish(packet.createBehov(BehovName.FORMAT_DOCUMENT, mapOf(DataFelt.RAW_DOCUMENT to packet[DataFelt.RAW_DOCUMENT])))
    }

}