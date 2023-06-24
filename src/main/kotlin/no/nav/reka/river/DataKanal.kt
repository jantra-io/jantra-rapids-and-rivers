package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Data

abstract class DataKanal(val rapidsConnection: RapidsConnection) : River.PacketListener {
    abstract val eventName: no.nav.reka.river.MessageType.Event

    init {
        configure(
            River(rapidsConnection).apply {
                validate(accept())
            }
        ).register(this)
    }

    abstract fun accept(): River.PacketValidation

    private fun configure(river: River): River {
        return river.validate {
            Data.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        onData(packet)
    }

    abstract fun onData(packet: JsonMessage)
}
