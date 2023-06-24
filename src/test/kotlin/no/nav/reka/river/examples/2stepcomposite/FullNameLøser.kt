package no.nav.reka.river.examples.twostepcomposite

import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.Løser

class FullNameLøser(rapidsConnection: RapidsConnection) : Løser(rapidsConnection) {
    override fun accept(): River.PacketValidation = River.PacketValidation {
        it.demandValue(Key.EVENT_NAME.str, Events.APPLICATION_INITIATED.name)
        it.demandValue(Key.BEHOV.str,Behov.FULL_NAME.name)
    }

    override fun onBehov(behov: no.nav.reka.river.model.Behov) {
        publishData(behov.createData(mapOf(DataFelt.NAME to "Alexander Petrov")))
    }

}