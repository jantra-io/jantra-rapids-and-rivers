package no.nav.reka.river.composite

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.IEventListener
import no.nav.reka.river.ValidatedMessage
import no.nav.reka.river.basic.EventListener
import no.nav.reka.river.model.Event

abstract class DelegatingEventListener(open val mainListener: MessageListener) : IEventListener,ValidatedMessage {

    override fun onEvent(packet: Event) {
        mainListener.onMessage(packet)
    }
}
