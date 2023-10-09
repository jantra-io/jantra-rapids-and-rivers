package no.nav.reka.river.examples.example_4_reacting_to_data_on_any_event.services

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
import no.nav.reka.river.publish

class JSONFormater(rapidsConnection: RapidsConnection) : Løser(rapidsConnection ) {

    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.BEHOV, BehovName.FORMAT_JSON)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onBehov(packet: Behov) {
       rapidsConnection.publish(packet.createData(mapOf(DataFelt.FORMATED_DOCUMENT to "My json formatted document")))
    }
}