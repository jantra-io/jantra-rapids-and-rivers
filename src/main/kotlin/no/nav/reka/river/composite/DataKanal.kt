package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.bridge.DataRiver
import no.nav.reka.river.IDataListener
import no.nav.reka.river.Key
import no.nav.reka.river.demandValue

abstract class DataKanal(val rapidsConnection: RapidsConnection) : IDataListener {
    abstract val eventName: MessageType.Event

    fun start() {
        DataRiver(rapidsConnection,this,accept()).start()
    }

    override fun accept() : River.PacketValidation {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME, eventName)
        }
    }

}
