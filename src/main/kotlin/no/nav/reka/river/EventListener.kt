package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.bridge.EventRiver

abstract class EventListener(val rapidsConnection: RapidsConnection) : IEventListener {

    abstract val event: MessageType.Event

    fun start() {
        EventRiver(rapidsConnection,this,accept() + { it.demandValue(Key.EVENT_NAME, event) }).start()
    }

    abstract override fun accept(): River.PacketValidation
    abstract override fun onEvent(packet: Event)

}
