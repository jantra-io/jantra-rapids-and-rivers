package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.model.Fail
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.IFailListener
import no.nav.reka.river.plus

abstract class FailKanal(val rapidsConnection: RapidsConnection) : IFailListener {
    abstract val eventName: MessageType.Event

    fun start() {
        FailRiver(rapidsConnection,this,accept() + { it.demandValue(Key.EVENT_NAME, eventName)}).start()
    }

    abstract override fun onFail(packet: Fail)
}
