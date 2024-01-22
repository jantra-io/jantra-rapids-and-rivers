package no.nav.jantra.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.jantra.river.model.Fail

open class DelegatingFailKanal(
    eventName: no.nav.jantra.river.MessageType.Event,
    private val mainListener: MessageListener,
    rapidsConnection: RapidsConnection
) : FailKanal(eventName,rapidsConnection) {
    override fun onFail(packet: Fail) {
        mainListener.onMessage(packet)
    }

}
