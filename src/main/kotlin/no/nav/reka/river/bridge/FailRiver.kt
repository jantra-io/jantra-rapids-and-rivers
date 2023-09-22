package no.nav.reka.river.bridge

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Fail
import no.nav.reka.river.IFailListener

class FailRiver (val rapidsConnection: RapidsConnection, val failListener: IFailListener, private val riverValidation: River.PacketValidation) : River.PacketListener{

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
        failListener.onFail(Fail.create(packet))
    }


}