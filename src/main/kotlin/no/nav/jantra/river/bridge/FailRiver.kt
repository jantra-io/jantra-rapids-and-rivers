package no.nav.jantra.river.bridge

import kotlin.concurrent.thread
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.model.Fail
import no.nav.jantra.river.IFailListener

class FailRiver (val rapidsConnection: RapidsConnection, val failListener: IFailListener, private val riverValidation: River.PacketValidation) : River.PacketListener{

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
            Fail.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        if (nonBlocking) {
            thread {
                failListener.onFail(Fail.create(packet))
            }
        }
        else {
            failListener.onFail(Fail.create(packet))
        }

    }


}