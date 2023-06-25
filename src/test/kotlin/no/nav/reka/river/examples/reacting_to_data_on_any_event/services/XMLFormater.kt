package no.nav.reka.river.examples.reacting_to_data_on_any_event.services

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

class XMLFormater(rapidsConnection: RapidsConnection) : Consumer(rapidsConnection) {

    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
        it.demandValue(Key.BEHOV, BehovName.FORMAT_XML)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onBehov(packet: Behov) {
        publishData(packet.createData(mapOf(DataFelt.FORMATED_DOCUMENT to "My XML formatted document")))
    }
}