package no.nav.reka.river.examples.services

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.Consumer
import no.nav.reka.river.demandValue
import no.nav.reka.river.examples.basic_consumer.BehovName
import no.nav.reka.river.examples.basic_consumer.DataFelt
import no.nav.reka.river.examples.basic_consumer.EventName

class RetrieveFullNameService(rapidsConnection: RapidsConnection) : Consumer(rapidsConnection) {
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME, EventName.APPLICATION_INITIATED)
        it.demandValue(Key.BEHOV, BehovName.FULL_NAME)
    }

    override fun onBehov(behov: no.nav.reka.river.model.Behov) {
        publishData(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
    }

}