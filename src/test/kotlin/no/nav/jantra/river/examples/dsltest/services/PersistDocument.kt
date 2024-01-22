package no.nav.jantra.river.examples.dsltest.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.publish

class PersistDocument(val rapidsConnection: RapidsConnection) : IBehovListener{



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