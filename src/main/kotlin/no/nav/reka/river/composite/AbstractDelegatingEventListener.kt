package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EventListener
import no.nav.reka.river.model.Event

abstract class AbstractDelegatingEventListener(open val mainListener: MessageListener, rapidsConnection: RapidsConnection) : EventListener(rapidsConnection) {

    override fun onEvent(packet: Event) {
        mainListener.onMessage(packet)
    }
}
