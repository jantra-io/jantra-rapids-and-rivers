package no.nav.jantra.river.examples.example_8_retrieving_data_from_client.services

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
import no.nav.jantra.river.publish
import java.lang.IllegalArgumentException

class PersistDocument(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.APPLICATION_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.BEHOV,BehovName.PERSIST_DOCUMENT)
        it.interestedIn(DataFelt.FORMATED_DOCUMENT)
    }

    private fun persistDocument(formatedDocument: String) : String {
        print("persisting formated document $formatedDocument")
        if (formatedDocument.contains("%")) {
            throw IllegalArgumentException("Illegal character detected.")
        }
        return "AB123"
    }
    override fun onBehov(behov: Behov) {
        try {
            val ref = persistDocument(behov[DataFelt.FORMATED_DOCUMENT].asText())
        }catch (ex: Exception) {
            rapidsConnection.publish(behov.createFail(ex.message!!))
            return
        }
        rapidsConnection.publish(behov.createData(mapOf(DataFelt.DOCUMENT_REFERECE to "AB123")))
    }
}