package no.nav.reka.river.newtest

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.EventListener
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Event
import no.nav.reka.river.test.IEventListener

class EventRiver(val rapidsConnection: RapidsConnection,val eventListener: IEventListener, private val riverValidation:River.PacketValidation) : River.PacketListener{

    fun start() {
        configureAsEventListener(
            River(rapidsConnection).apply {
                validate(riverValidation)
            }
        ).register(this)
    }

    private fun configureAsEventListener(river: River): River {
        return river.validate {
            Event.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        eventListener.onEvent(Event.create(packet))
    }


}