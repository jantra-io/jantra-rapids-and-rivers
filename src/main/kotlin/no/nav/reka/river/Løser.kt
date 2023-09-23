package no.nav.reka.river

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.bridge.BehovRiver

abstract class Løser(val rapidsConnection: RapidsConnection) : IBehovListener {

    fun start() {
        BehovRiver(rapidsConnection,this,accept()).start()
    }



}
