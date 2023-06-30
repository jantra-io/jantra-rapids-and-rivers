package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Fail

abstract class FailKanal(val rapidsConnection: RapidsConnection) : River.PacketListener {
    abstract val eventName: MessageType.Event

    init {
        configure(
            River(rapidsConnection).apply {
                validate(accept())
            }
        ).register(this)
    }

    protected fun accept(): River.PacketValidation {
        return River.PacketValidation { }
    }

    private fun configure(river: River): River {
        return river.validate {
            Fail.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        onFail(Fail.create(packet))
    }

    abstract fun onFail(packet: Fail)
}
