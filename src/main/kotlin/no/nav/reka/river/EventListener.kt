package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail

abstract class EventListener(val rapidsConnection: RapidsConnection) : River.PacketListener {

    abstract val event: MessageType.Event

    init {
        configureAsListener(
            River(rapidsConnection).apply {
                validate(accept())
            }
        ).register(this)
    }

    abstract fun accept(): River.PacketValidation

    private fun configureAsListener(river: River): River {
        return river.validate {
            Event.packetValidator.validate(it)
        }
    }
    fun publishBehov(message: Behov) {
        rapidsConnection.publish(message.toJsonMessage().toJson())
    }

    fun publish(message: JsonMessage) {
        rapidsConnection.publish(message.toJson())
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        onEvent(Event.create(packet))
    }

    abstract fun onEvent(packet: Event)

    fun publishFail(fail: Fail) {
        rapidsConnection.publish(fail.toJsonMessage().toJson())
    }
}
