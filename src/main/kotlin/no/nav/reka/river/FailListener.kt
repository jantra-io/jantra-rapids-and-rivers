package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.bridge.EventRiver
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Fail

abstract class FailListener(val rapidsConnection: RapidsConnection) : IFailListener {
    fun start() {
        FailRiver(rapidsConnection,this,accept()).start()
    }

}