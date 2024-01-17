package no.nav.reka.river.examples.example_8_retrieving_data_from_client.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.basic.Løser
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.interestedIn
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.publish
import no.nav.reka.river.redis.RedisStore
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