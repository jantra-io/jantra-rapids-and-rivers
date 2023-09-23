package no.nav.reka.river.examples.composite_med_fail_listener.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Løser
import no.nav.reka.river.Key
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.interestedIn
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.publish

class PersistDocument(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {
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
        rapidsConnection.publish(
            Event.create(EventName.DOCUMENT_PERSISTED, mapOf(DataFelt.DOCUMENT_REFERECE to "AB123"))
        )
    }
}