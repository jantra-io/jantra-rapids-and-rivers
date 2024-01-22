package no.nav.jantra.river.examples.example_4_reacting_to_data_on_any_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.*
import no.nav.jantra.river.basic.DataConsumer
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Data
import no.nav.jantra.river.model.Event

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
    override fun onData(data: Data) {
        val ref = persistDocument(data[DataFelt.FORMATED_DOCUMENT].asText())
        rapidsConnection.publish(Event.create(EventName.DOCUMENT_PERSISTED, mapOf(Key.RIVER_ORIGIN to data.riverId(),DataFelt.DOCUMENT_REFERECE to ref)))
    }
}