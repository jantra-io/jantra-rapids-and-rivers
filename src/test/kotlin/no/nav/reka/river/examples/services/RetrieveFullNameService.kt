package no.nav.reka.river.examples.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.basic.Løser
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.example_1_basic_løser.BehovName
import no.nav.reka.river.examples.example_1_basic_løser.DataFelt
import no.nav.reka.river.examples.example_1_basic_løser.EventName
import no.nav.reka.river.publish

class RetrieveFullNameService(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.APPLICATION_INITIATED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, event)
        it.demandValue(Key.BEHOV, BehovName.FULL_NAME)
    }

    override fun onBehov(behov: no.nav.reka.river.model.Behov) {
        rapidsConnection.publish(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
    }

}