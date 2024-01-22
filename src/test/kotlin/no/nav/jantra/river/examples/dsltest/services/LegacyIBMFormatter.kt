package no.nav.jantra.river.examples.dsltest.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.Key
import no.nav.jantra.river.ValidatedMessage
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.interestedIn
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.publish

class LegacyIBMFormatter(val rapidsConnection: RapidsConnection): IBehovListener, ValidatedMessage {

    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.DOCUMENT_RECIEVED)
        it.demandValue(Key.BEHOV, BehovName.FORMAT_DOCUMENT_IBM)
        it.interestedIn(DataFelt.RAW_DOCUMENT)
        it.interestedIn(DataFelt.RAW_DOCUMENT_FORMAT)
    }

    override fun onBehov(packet: Behov) {

            packet.createBehov(
                BehovName.PERSIST_DOCUMENT,
                mapOf(DataFelt.FORMATED_DOCUMENT to "This is my IBM formated document")
            ).also { rapidsConnection.publish(it) }

    }


}