package no.nav.reka.river.examples.capture_fail_from_listener.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Løser
import no.nav.reka.river.Key
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName
import no.nav.reka.river.interestedIn
import no.nav.reka.river.model.Behov
import no.nav.reka.river.publish

class LegacyIBMFormatter(rapidsConnection: RapidsConnection): Løser(rapidsConnection) {
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
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