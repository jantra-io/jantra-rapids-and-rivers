package no.nav.jantra.river.examples.services.basic

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.Key
import no.nav.jantra.river.basic.Løser
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.examples.example_1_basic_løser.BehovName
import no.nav.jantra.river.examples.example_1_basic_løser.DataFelt
import no.nav.jantra.river.examples.example_1_basic_løser.EventName
import no.nav.jantra.river.model.Behov
import no.nav.jantra.river.publish

class RetrieveFullNameService(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {

    override val event: MessageType.Event = EventName.APPLICATION_INITIATED
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, event)
        it.demandValue(Key.BEHOV, BehovName.FULL_NAME)
    }

    override fun onBehov(behov: no.nav.jantra.river.model.Behov) {
        rapidsConnection.publish(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
    }

}

class RetrieveFullNameServiceDelegate(val rapidsConnection: RapidsConnection) : IBehovListener {
    override fun onBehov(behov: Behov) {
        rapidsConnection.publish(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
    }

}