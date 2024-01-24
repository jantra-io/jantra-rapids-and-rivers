package no.nav.jantra.river.examples.example_3_event_triger_2_behov_emitting_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.basic.Løser
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.interestedIn
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.publish

class PersistDocument(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.BEHOV,BehovName.PERSIST_DOCUMENT)
        it.interestedIn(DataFelt.FORMATED_DOCUMENT)
    }

    private fun persistDocument(formatedDocument: String) : String {
        print("persisting formated document $formatedDocument")
        return "AB123"
    }
    override fun onBehov(packet: Behov) {
        val ref = persistDocument(packet[DataFelt.FORMATED_DOCUMENT].asText())
        rapidsConnection.publish(Event.create(EventName.DOCUMENT_PERSISTED,  mapOf(Key.RIVER_ORIGIN to packet.riverId(),Key.APP_KEY to "AB123", DataFelt.DOCUMENT_REFERECE to "AB123")))
    }
}