package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Data
import no.nav.reka.river.newtest.DataRiver
import no.nav.reka.river.test.IDataListener

abstract class DataKanal(val rapidsConnection: RapidsConnection) : River.PacketListener, IDataListener {
    abstract val eventName: MessageType.Event

    fun start() {
        DataRiver(rapidsConnection,this,accept()).start()
    }

    abstract override fun accept(): River.PacketValidation

    private fun configure(river: River): River {
        return river.validate {
            Data.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        onData(Data.create(packet))
    }

    abstract override fun onData(data: Data)
}
