package no.nav.reka.river.configuration

import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.composite.MessageListener
import no.nav.reka.river.model.Event
import no.nav.reka.river.model.Fail
import no.nav.reka.river.model.Message
import no.nav.reka.river.test.IEventListener
import no.nav.reka.river.test.IFailListener

class EventListener(val onEventListener: IEventListener,val onFail: IFailListener) : MessageListener, IEventListener, IFailListener {



    override fun onMessage(message: Message) {
        when (message) {
            is Event ->  onEvent(message)
            is Fail  ->  onFail(message)
            else -> {
                println("Illegal message type")
            }
        }
    }

    override fun onEvent(event: Event) {
        onEventListener.onEvent(event)
    }

    override fun onFail(fail: Fail) {
        onFail.onFail(fail)
    }

    override fun accept(): River.PacketValidation {
        TODO("Not yet implemented")
    }

}