package no.nav.reka.river.newtest

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Fail
import no.nav.reka.river.IFailListener

class FailRiver (val rapidsConnection: RapidsConnection, val failListener: IFailListener, private val riverValidation: River.PacketValidation) : River.PacketListener{

    fun start() {
        configureAsFailListener(
            River(rapidsConnection).apply {
                validate(riverValidation)
            }
        ).register(this)
    }

    private fun configureAsFailListener(river: River): River {
        return river.validate {
            Fail.packetValidator.validate(it)
        }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        failListener.onFail(Fail.create(packet))
    }


}