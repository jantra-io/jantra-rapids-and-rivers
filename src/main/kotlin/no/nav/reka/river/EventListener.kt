package no.nav.reka.river

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.newtest.EventRiver

abstract class EventListener(val rapidsConnection: RapidsConnection) : IEventListener {

    abstract val event: MessageType.Event

    fun start() {
        EventRiver(rapidsConnection,this,accept()).start()
    }

    abstract override fun accept(): River.PacketValidation
    fun publishBehov(message: Behov) {
        rapidsConnection.publish(message.toJsonMessage().toJson())
    }

    fun publish(message: JsonMessage) {
        rapidsConnection.publish(message.toJson())
    }
    
    abstract override fun onEvent(packet: Event)

    fun publishFail(fail: Fail) {
        rapidsConnection.publish(fail.toJsonMessage().toJson())
    }
}
