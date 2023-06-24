package no.nav.reka.river.examples.basic_consumer

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.Consumer
import no.nav.reka.river.examples.BehovName
import no.nav.reka.river.examples.EventName

class FullNameConsumer(rapidsConnection: RapidsConnection) : Consumer(rapidsConnection) {
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME.str, EventName.APPLICATION_INITIATED.name)
        it.demandValue(Key.BEHOV.str, BehovName.FULL_NAME.name)
    }

    override fun onBehov(behov: no.nav.reka.river.model.Behov) {
        publishData(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
    }

}