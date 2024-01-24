package no.nav.jantra.river.bridge

import kotlin.concurrent.thread
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.model.Behov

class BehovRiver(val rapidsConnection: RapidsConnection, val behovListener:IBehovListener, private val riverValidation: River.PacketValidation) : River.PacketListener {

    var nonBlocking:Boolean = true

    fun start() {
        configure(
            River(rapidsConnection).apply {
                validate(riverValidation)
            }
        ).register(this)
    }

    private fun configure(river: River): River {
        return river.validate {
            Behov.packetValidator.validate(it)
        }
    }
    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        if (nonBlocking) {
            thread {
                behovListener.onBehov(Behov.create(packet))
            }
        }
        else {
            behovListener.onBehov(Behov.create(packet))
        }

    }
}