package no.nav.reka.river.configuration

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.reka.river.EventListener
import no.nav.reka.river.model.Event
import no.nav.reka.river.test.IEventListener

abstract class DelegatingEventListener2(open val mainListener: IEventListener, rapidsConnection: RapidsConnection) : EventListener(rapidsConnection) {

    override fun onEvent(packet: Event) {
        mainListener.onEvent(packet)
    }
}
