package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Fail

open class DelegatingFailKanal(
    eventName: no.nav.reka.river.MessageType.Event,
    private val mainListener: MessageListener,
    rapidsConnection: RapidsConnection
) : FailKanal(eventName,rapidsConnection) {
    override fun onFail(packet: Fail) {
        mainListener.onMessage(packet)
    }

}
