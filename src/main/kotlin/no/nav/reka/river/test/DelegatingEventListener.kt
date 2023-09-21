package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.model.Event

class DelegatingEventListener(val mainListener: IEventListener, rapidsConnection: RapidsConnection,
                              override val event: MessageType.Event, val packageValidator : River.PacketValidation
) : no.nav.reka.river.EventListener(rapidsConnection) {
    override fun accept(): River.PacketValidation {
        return packageValidator
    }

    override fun onEvent(packet: Event) {
      //  mainListener.
    }


}