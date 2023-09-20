package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.composite.DelegatingFailKanal
import no.nav.reka.river.composite.MessageListener
import no.nav.reka.river.demandValue
import no.nav.reka.river.model.Behov
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.Message

abstract class EventListenerWithFail(val rapidsConnection: RapidsConnection) : MessageListener, IEventListener, IFailListener{

    abstract val event: MessageType.Event

    init {
        val validation: River.PacketValidation = River.PacketValidation {
            this@EventListenerWithFail.accept().validate(it)
        }

       DelegatingEventListener(this,rapidsConnection,event, validation).start()

    }

    abstract override fun accept() :River.PacketValidation

    abstract override fun onEvent(event: Event)
    open override fun onFail(fail: Fail) {}
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
        object:DelegatingFailKanal(event,this, rapidsConnection) {
            override fun accept(): River.PacketValidation {
                return River.PacketValidation {
                    it.demandValue(Key.EVENT_NAME, eventName)
                    this@EventListenerWithFail.accept().validate(it)
                }
            }
        }
        return this
    }

    fun publishBehov(message: Behov) {
        rapidsConnection.publish(message.toJsonMessage().toJson())
    }

    fun publish(message: JsonMessage) {
        rapidsConnection.publish(message.toJson())
    }

    fun publishFail(fail: Fail) {
        rapidsConnection.publish(fail.toJsonMessage().toJson())
    }
}