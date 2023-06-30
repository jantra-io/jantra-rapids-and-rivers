package no.nav.reka.river.test

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.MessageType
import no.nav.reka.river.composite.DelegatingFailKanal
import no.nav.reka.river.composite.MessageListener
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.Message

open class MessageListener2(val event: MessageType.Event,val rapidsConnection: RapidsConnection) : MessageListener {

    fun configureListener(packetValidation: River.PacketValidation) : MessageListener2 {
        DelegatingEventListener(this, rapidsConnection, event, packetValidation)
        return this
    }

    fun configureFailListener() : MessageListener2 {
        DelegatingFailKanal(event, this, rapidsConnection)
        return this
    }

    fun onEvent() {}
    fun onFail() {}

    override fun onMessage(message: Message) {
        when (message) {
            is Event ->  onEvent()
            is Fail ->  onFail()
            else -> {
                println("Illegal message type")
            }
        }
    }
}