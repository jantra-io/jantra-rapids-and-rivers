package no.nav.jantra.river.bridge

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.model.Event
import no.nav.jantra.river.IEventListener
import no.nav.jantra.river.InternalBehov
import no.nav.jantra.river.publish
import java.util.*

class EventRiver(val rapidsConnection: RapidsConnection, val eventListener: IEventListener, private val riverValidation:River.PacketValidation) : River.PacketListener{

    fun start() {
        configure(
            River(rapidsConnection).apply {
                validate(riverValidation)
            }
        ).register(this)
    }

    private fun configure(river: River): River {
        return river.validate {
            Event.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val event = Event.create(packet)
        val uuid = UUID.randomUUID().toString()
        event.riverId = uuid
        val createRiver = event.createBehov(InternalBehov("create-river"), mapOf())
        println("Creating river")
        rapidsConnection.publish(createRiver)
        eventListener.onEvent(event)
    }


}