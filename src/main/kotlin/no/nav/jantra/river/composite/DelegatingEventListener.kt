package no.nav.jantra.river.composite

import no.nav.jantra.river.IEventListener
import no.nav.jantra.river.ValidatedMessage
import no.nav.jantra.river.model.Event

abstract class DelegatingEventListener(open val mainListener: MessageListener) : IEventListener,ValidatedMessage {

    override fun onEvent(packet: Event) {
        mainListener.onMessage(packet)
    }
}
