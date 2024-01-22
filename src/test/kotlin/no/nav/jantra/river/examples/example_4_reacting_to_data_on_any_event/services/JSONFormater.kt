package no.nav.jantra.river.examples.example_4_reacting_to_data_on_any_event.services

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