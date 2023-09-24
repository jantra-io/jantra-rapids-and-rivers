package no.nav.reka.river.examples.composite_med_fail_listener.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.isMissingOrNull
import no.nav.reka.river.basic.Løser
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.interestedIn
import no.nav.reka.river.model.Behov
import no.nav.reka.river.publish

class FormatDokumentService(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
        it.demandValue(Key.BEHOV,BehovName.FORMAT_DOCUMENT)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
        it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
    }

    private fun formatDocument(rawDocument:String) {
        println("Document is now formated $rawDocument")
    }

    override fun onBehov(packet: Behov) {
        formatDocument(packet[DataFelt.RAW_DOCUMENT].asText())
        val documentFormat = packet[DataFelt.RAW_DOCUMENT_FORMAT].takeUnless { it.isMissingOrNull() }?.asText()
        if (documentFormat != "ebcdic") {
            rapidsConnection.publish(
                packet.createBehov(
                    BehovName.PERSIST_DOCUMENT,
                    mapOf(DataFelt.FORMATED_DOCUMENT to "This is my formated document")
                )
            )
        }
        else {
            rapidsConnection.publish(
             packet.createFail("Unable to process files with EBCDIC charset")
            )
        }
    }


}