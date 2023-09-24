package no.nav.reka.river.examples.reacting_to_data_on_any_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.*
import no.nav.reka.river.basic.DataConsumer
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event

class PersistDocument(rapidsConnection: RapidsConnection) : DataConsumer(rapidsConnection) {
    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandKey(Key.DATA.str)
        it.demandKey(DataFelt.FORMATED_DOCUMENT.str)
    }

    private fun persistDocument(formatedDocument: String) : String {
        print("persisting formated document $formatedDocument")
        return "AB123"
    }
    override fun onData(packet: Data) {
        val ref = persistDocument(packet[DataFelt.FORMATED_DOCUMENT].asText())
        rapidsConnection.publish(Event.create(EventName.DOCUMENT_PERSISTED, mapOf(DataFelt.DOCUMENT_REFERECE to ref)))
    }
}