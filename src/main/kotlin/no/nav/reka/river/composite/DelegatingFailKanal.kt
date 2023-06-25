package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class DelegatingFailKanal(
    override val eventName: no.nav.reka.river.MessageType.Event,
    private val mainListener: River.PacketListener,
    rapidsConnection: RapidsConnection
) : FailKanal(rapidsConnection) {
    override fun onFail(packet: JsonMessage) {
        mainListener.onPacket(packet, rapidsConnection)
    }
}
