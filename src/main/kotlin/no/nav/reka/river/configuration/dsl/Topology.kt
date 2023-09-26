package no.nav.reka.river.configuration.dsl

import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener
import no.nav.reka.river.IMessageListener
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.bridge.BehovRiver
import no.nav.reka.river.bridge.DataRiver
import no.nav.reka.river.bridge.EventRiver
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.demandValue
import no.nav.reka.river.plus

class TopologyBuilder(private val rapid: RapidsConnection,
    val eventListeners: List<EventRiver> = mutableListOf(),
    val dataListensers: List<DataRiver> = mutableListOf(),
    val faillisteners: List<FailRiver> = mutableListOf()) {

    @DSLTopology
    fun composition(name: String = "",block: CompositionBuilder.() -> Unit) {
        CompositionBuilder(rapid).apply(block)
    }
}

class CompositionBuilder(private val rapid: RapidsConnection,
                         val løser: List<BehovRiver> = mutableListOf(),
                         val dataListensers: List<DataRiver> = mutableListOf()
                         val faillisteners: List<FailRiver> = mutableListOf()
) {
    private lateinit var eventListener:EventRiver
   

    @DSLTopology
    fun eventListener(eventName: MessageType.Event ,block: EventListenerBuilder.()-> Unit) {
        eventListener = EventListenerBuilder(eventName,rapid).apply(block).build()
    }

    @DSLTopology
    fun failListener(eventName: MessageType.Event,block: EventListenerBuilder.()-> Unit) {
        eventListener = EventListenerBuilder(eventName,rapid).apply(block).build()
    }

    fun build(): List<IMessageListener> {
        val messageListeners = mutableListOf<IMessageListener>().apply {
            this.addAll(faillisteners.map { it as IMessageListener })
            this.addAll(løser.map { it as IMessageListener })
            this.add(eventListener as IMessageListener)
        }
        return mutableListOf()
    }
}

class EventListenerBuilder(private val eventName: MessageType.Event, private val rapid: RapidsConnection) {
    @DSLTopology
    lateinit var implementation: IEventListener
    lateinit var event: MessageType.Event
    private lateinit var accept: River.PacketValidation

    @DSLTopology
    fun accepts(packetValidation: River.PacketValidation)  {
        accept = packetValidation
    }

    internal fun build() : EventRiver {
        val validation = if (::accept.isInitialized) accept else implementation.accept() + River.PacketValidation{it.demandValue(
            Key.EVENT_NAME,event)}
        return EventRiver(rapid,implementation, validation)
    }

}

class FailListenerBuilder( private val rapid: RapidsConnection) {
    @DSLTopology
    lateinit var implementation: IFailListener
    lateinit var event: MessageType.Event
    private lateinit var accept: River.PacketValidation

    @DSLTopology
    fun accepts(packetValidation: River.PacketValidation)  {
        accept = packetValidation
    }

    internal fun build() : EventRiver {
        val validation = if (::accept.isInitialized) accept else implementation.accept() + River.PacketValidation{it.demandValue(
            Key.EVENT_NAME,event)}
        return EventRiver(rapid,implementation, validation)
    }

}


@DslMarker
annotation class DSLTopology

@DSLTopology
fun topology(rapid: RapidsConnection, block: TopologyBuilder.() -> Unit) : TopologyBuilder = TopologyBuilder(rapid).apply(block)

