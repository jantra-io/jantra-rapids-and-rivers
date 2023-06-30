package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.composite.DelegatingEventListener
import no.nav.reka.river.composite.DelegatingFailKanal
import no.nav.reka.river.composite.MessageListener
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.Message

abstract class EventListener(val event: MessageType.Event, val rapidsConnection: RapidsConnection) : MessageListener{

    init {
       no.nav.reka.river.test.DelegatingEventListener(this,rapidsConnection,event) {
           accept()
       }

    }

    abstract fun accept() :River.PacketValidation

    abstract fun onEvent(event: Event)
    abstract fun onFail(fail: Fail)
    override fun onMessage(message: Message) {
        when (message) {
            is Event ->  onEvent(message as Event)
            is Fail  ->  onFail(message as Fail)
            else -> {
                println("Illegal message type")
            }
        }
    }
   fun withFailhandling() : MessageListener {
        DelegatingFailKanal(event,this, rapidsConnection)
        return this
    }
}