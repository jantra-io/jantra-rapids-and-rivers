package no.nav.reka.river.examples.example_5_capture_fail_from_listener.services

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

class LegacyIBMFormatter(rapidsConnection: RapidsConnection): Løser(rapidsConnection) {

    override val event: MessageType.Event
        get() = EventName.DOCUMENT_RECIEVED

    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.BEHOV, BehovName.FORMAT_DOCUMENT_IBM)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
        it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
    }

    override fun onBehov(packet: Behov) {
        rapidsConnection.publish(
            packet.createBehov(
                BehovName.PERSIST_DOCUMENT,
                mapOf(DataFelt.FORMATED_DOCUMENT to "This is my IBM formated document")
            )
        )
    }


}