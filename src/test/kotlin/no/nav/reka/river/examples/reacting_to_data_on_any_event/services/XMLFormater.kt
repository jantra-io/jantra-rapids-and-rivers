package no.nav.reka.river.examples.reacting_to_data_on_any_event.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
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

class XMLFormater(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
        it.demandValue(Key.BEHOV, BehovName.FORMAT_XML)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onBehov(packet: Behov) {
        rapidsConnection.publish(packet.createData(mapOf(DataFelt.FORMATED_DOCUMENT to "My XML formatted document")))
    }
}