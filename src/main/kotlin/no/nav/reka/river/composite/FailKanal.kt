package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.demandValue
import no.nav.reka.river.model.Fail
import no.nav.reka.river.test.IFailListener

abstract class FailKanal(val rapidsConnection: RapidsConnection) : River.PacketListener, IFailListener {
    abstract val eventName: MessageType.Event

    init {
        configure(
            River(rapidsConnection).apply {
                validate(accept())
            }
        ).register(this)
    }

    override fun accept(): River.PacketValidation {
        return River.PacketValidation {
            it.demandValue(Key.EVENT_NAME, eventName)
        }
    }

    private fun configure(river: River): River {
        return river.validate {
            Fail.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        onFail(Fail.create(packet))
    }

    abstract override fun onFail(packet: Fail)
}
