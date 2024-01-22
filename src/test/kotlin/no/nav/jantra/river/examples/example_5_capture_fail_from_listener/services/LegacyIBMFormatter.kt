package no.nav.jantra.river.examples.example_5_capture_fail_from_listener.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.publish

class LegacyIBMFormatter(val rapidsConnection: RapidsConnection): IBehovListener {


    override fun onBehov(packet: Behov) {
        rapidsConnection.publish(
            packet.createBehov(
                BehovName.PERSIST_DOCUMENT,
                mapOf(DataFelt.FORMATED_DOCUMENT to "This is my IBM formated document")
            )
        )
    }


}