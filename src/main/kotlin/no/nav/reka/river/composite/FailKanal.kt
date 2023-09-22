package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.model.Fail
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.IFailListener

abstract class FailKanal(val rapidsConnection: RapidsConnection) : IFailListener {
    abstract val eventName: MessageType.Event

    fun start() {
        FailRiver(rapidsConnection,this,accept()).start()
    }

    override fun accept(): River.PacketValidation {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME, eventName)
        }
    }


    abstract override fun onFail(packet: Fail)
}
