package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.configuration.DelegatingEventListener2

class DelegatingEventListener(override val mainListener: IEventListener, rapidsConnection: RapidsConnection,
                              override val event: MessageType.Event, val packageValidator : River.PacketValidation
) : DelegatingEventListener2(mainListener,rapidsConnection) {
    override fun accept(): River.PacketValidation {
        return packageValidator
    }


}