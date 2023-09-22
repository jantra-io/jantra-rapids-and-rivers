package no.nav.reka.river

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.bridge.BehovRiver

abstract class Consumer(val rapidsConnection: RapidsConnection) : IBehovListener {


    fun start() {
        BehovRiver(rapidsConnection,this,accept()).start()
    }

    fun publishData(data: Data) {
        rapidsConnection.publish(data.toJsonMessage().toJson())
    }

    fun publishBehov(behov: Behov) {
        rapidsConnection.publish(behov.toJsonMessage().toJson())
    }

    fun publishEvent(event: Event) {
        rapidsConnection.publish(event.toJsonMessage().toJson())
    }

    fun publishFail(fail: Fail) {
        rapidsConnection.publish(fail.toJsonMessage().toJson())
    }


}
