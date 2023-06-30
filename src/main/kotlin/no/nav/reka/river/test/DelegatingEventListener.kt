package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.composite.DelegatingEventListener
import no.nav.reka.river.composite.MessageListener
import no.nav.reka.river.model.Event

class DelegatingEventListener(override val mainListener: MessageListener, rapidsConnection: RapidsConnection,
                              override val event: MessageType.Event, val packageValidator : River.PacketValidation
) : DelegatingEventListener(mainListener,rapidsConnection) {
    override fun accept(): River.PacketValidation {
        return packageValidator
    }


}