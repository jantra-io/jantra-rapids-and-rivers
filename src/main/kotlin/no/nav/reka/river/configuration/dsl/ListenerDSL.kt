package no.nav.reka.river.configuration.dsl

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.bridge.DataRiver
import no.nav.reka.river.bridge.EventRiver
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.demandValue
import no.nav.reka.river.plus



class ListenerDSLBuilder(private val rapid:RapidsConnection) {
    @DSLListener
    lateinit var event: MessageType.Event
    private lateinit var eventRiver: EventRiver
    private lateinit var dataRiver: DataRiver
    private lateinit var failRiver: FailRiver
    @DSLListener
    fun eventListener(block: EventListenerDSLBuilder.()->Unit)  {
        eventRiver = EventListenerDSLBuilder(event,rapid).apply(block).build()
    }
    @DSLListener
    fun failListener(block: FailListenerDSLBuilder.()->Unit) {
        failRiver = FailListenerDSLBuilder(event,rapid).apply(block).build()
    }

    fun start() {
        if (::eventRiver.isInitialized) eventRiver.start()
        if (::dataRiver.isInitialized)  dataRiver.start()
        if (::failRiver.isInitialized)  failRiver.start()
    }
}

class EventListenerDSLBuilder(private val event: MessageType.Event, private val rapid: RapidsConnection) {
    @DSLListener
    lateinit var implementation: IEventListener
    private lateinit var accept: River.PacketValidation
    @DSLListener
    fun accepts(packetValidation: River.PacketValidation)  {
        accept = packetValidation
    }

    internal fun build() : EventRiver {
        val validation = if (::accept.isInitialized) accept else implementation.accept() + River.PacketValidation{it.demandValue(
            Key.EVENT_NAME,event)}
        return EventRiver(rapid,implementation, validation)
    }

}

class FailListenerDSLBuilder(private val event: MessageType.Event, private val rapid: RapidsConnection) {
    private lateinit var accept: River.PacketValidation
    @DSLListener
    lateinit var implementation: IFailListener


    @DSLListener
    fun accepts(packetValidation: River.PacketValidation)  {
        accept = packetValidation
    }

    fun build() : FailRiver {
        if (!this::accept.isInitialized) accept = River.PacketValidation {
            it.demandValue(Key.EVENT_NAME.str,event.value)
        }
        return FailRiver(rapid,implementation,accept)
    }

}

@DslMarker
annotation class DSLListener

@DSLListener
fun listener( rapid:RapidsConnection, block: ListenerDSLBuilder.() -> Unit) : ListenerDSLBuilder = ListenerDSLBuilder(rapid).apply(block)

