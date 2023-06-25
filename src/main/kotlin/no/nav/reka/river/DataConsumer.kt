package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Data
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail

abstract class DataConsumer(val rapidsConnection: RapidsConnection): River.PacketListener {
    init {
        configure(
                River(rapidsConnection).apply {
                    validate(accept())
                }
        ).register(this)
    }

    abstract fun accept(): River.PacketValidation

    private fun configure(river: River): River {
        return river.validate {
            Data.packetValidator.validate(it)
        }
    }

    fun publishData(data: Data) {
        rapidsConnection.publish(data.toJsonMessage().toJson())
    }

    fun publishBehov(behov: Behov) {
        rapidsConnection.publish(behov.toJsonMessage().toJson())
    }

    fun publishEvent(event: Event) {
        rapidsConnection.publish(event.toJsonMessage().toJson())
    }

    fun publishFail(fail: Fail) {
        rapidsConnection.publish(fail.toJsonMessage().toJson())
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        onData(Data.create(packet))
    }

    abstract fun onData(packet: Data)
}