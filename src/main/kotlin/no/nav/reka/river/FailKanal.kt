package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Fail

// vi kan vurdere å bruke event feltet og dispatche event istedenfor Fail.
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
        onFail(packet)
    }

    abstract fun onFail(packet: JsonMessage)
}
