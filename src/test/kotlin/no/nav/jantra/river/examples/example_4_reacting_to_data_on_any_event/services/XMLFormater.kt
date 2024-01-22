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

class XMLFormater(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.DOCUMENT_RECIEVED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
        it.demandValue(Key.BEHOV, BehovName.FORMAT_XML)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
    }

    override fun onBehov(behov: Behov) {
        rapidsConnection.publish(behov.createData(mapOf(DataFelt.FORMATED_DOCUMENT to "My XML formatted document")))
    }
}