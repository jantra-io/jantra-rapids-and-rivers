package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.test.IFailListener

abstract class FailListener(val rapidsConnection: RapidsConnection) : IFailListener {
    override fun onFail(fail: Fail) {
        TODO("Not yet implemented")
    }

    fun publishBehov(message: Behov) {
        rapidsConnection.publish(message.toJsonMessage().toJson())
    }

    fun publish(message: JsonMessage) {
        rapidsConnection.publish(message.toJson())
    }
    fun publishFail(fail: Fail) {
        rapidsConnection.publish(fail.toJsonMessage().toJson())
    }

}