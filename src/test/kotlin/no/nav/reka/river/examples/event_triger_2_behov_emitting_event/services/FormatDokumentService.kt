package no.nav.reka.river.examples.event_triger_2_behov_emitting_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Consumer
import no.nav.reka.river.Key
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.interestedIn
import no.nav.reka.river.model.Behov

class FormatDokumentService(rapidsConnection: RapidsConnection) : Consumer(rapidsConnection) {
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
        it.demandValue(Key.BEHOV,BehovName.FORMAT_DOCUMENT)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    private fun formatDocument(rawDocument:String) {
        println("Document is now formated $rawDocument")
    }

    override fun onBehov(packet: Behov) {
        formatDocument(packet[DataFelt.RAW_DOCUMENT].asText())
        publishBehov(packet.createBehov(BehovName.PERSIST_DOCUMENT, mapOf(DataFelt.FORMATED_DOCUMENT to "This is my formated document")))
    }


}