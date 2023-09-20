package no.nav.reka.river.configuration

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.MessageType
import no.nav.reka.river.test.DelegatingEventListener
import no.nav.reka.river.test.IEventListener
import no.nav.reka.river.test.IFailListener


class ListenerConfigurer(event: MessageType.Event) {
 lateinit var event: MessageType.Event
 lateinit var failListener: FailListenerConfiguration
 lateinit var eventListener: EventListenerConfiguration
 lateinit var dataListener: DataListenerConfiguration

     fun withFailListener( ) : FailListenerConfiguration {
         failListener = FailListenerConfiguration(this)
         return failListener
     }

    fun withEventlistenerConfig() : EventListenerConfiguration {
        return EventListenerConfiguration()
    }

}

class DataListenerConfiguration(private val listenerConfiguration: ListenerConfigurer) {
    lateinit var dataFelter: Array<IDataFelt>
}


class FailListenerConfiguration(private val listenerConfiguration: ListenerConfigurer){
   lateinit var failListener: IFailListener
   lateinit var accept : River.PacketValidation

   fun build(): ListenerConfigurer {
       return listenerConfiguration
   }
}
class EventListenerConfiguration {
    lateinit var event: MessageType.Event
    lateinit var accept : River.PacketValidation
    lateinit var onEvent : IEventListener

    fun eventConfiguration( event: MessageType.Event): EventListenerConfiguration {
        this.event = event
        return this
    }

    fun accept(validationRules : River.PacketValidation) : EventListenerConfiguration {
        accept = validationRules
        return this
    }

    fun onEvent(onEvent: IEventListener) : EventListenerConfiguration {
        this.onEvent = onEvent
        return this
    }

    fun build(rapidsConnection: RapidsConnection): IEventListener {
        return DelegatingEventListener(onEvent,rapidsConnection,this.event,accept)
    }




}