package no.nav.reka.river.examples.dsltest.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.isMissingOrNull
import no.nav.reka.river.IBehovListener
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.model.Behov
import no.nav.reka.river.publish

class FormatDokumentService(val rapidsConnection: RapidsConnection) : IBehovListener {


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