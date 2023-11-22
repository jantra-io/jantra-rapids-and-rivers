package no.nav.reka.river.configuration.dsl

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.reka.river.IBehovListener
import no.nav.reka.river.IDataFelt
import no.nav.reka.river.IDataListener
import no.nav.reka.river.IEventListener
import no.nav.reka.river.IFailListener
import no.nav.reka.river.IMessageListener
import no.nav.reka.river.Key
import no.nav.reka.river.MessageType
import no.nav.reka.river.basic.Løser
import no.nav.reka.river.bridge.BehovRiver
import no.nav.reka.river.bridge.DataRiver
import no.nav.reka.river.bridge.EventRiver
import no.nav.reka.river.bridge.FailRiver
import no.nav.reka.river.demandValue
import no.nav.reka.river.interestedIn
import no.nav.reka.river.plus

@DSLTopology
class TopologyBuilder(private val rapid: RapidsConnection) {

    private lateinit var compositionBuilder : CompositionBuilder
    @DSLTopology
    fun composition(name: String = "",block: CompositionBuilder.() -> Unit) {
        compositionBuilder = CompositionBuilder(rapid).apply(block)
    }
    fun start() {
        compositionBuilder.start()
    }
}

@DSLTopology
class CompositionBuilder(private val rapid: RapidsConnection,
                         val løser: List<BehovRiver> = mutableListOf(),
                         val dataListensers: MutableList<DataRiver> = mutableListOf(),
                         val faillisteners: MutableList<FailRiver> = mutableListOf()
) {
    private lateinit var eventListener:EventRiver


    @DSLTopology
    fun eventListener(eventName: MessageType.Event ,block: EventListenerBuilder.()-> Unit) {
        eventListener = EventListenerBuilder(eventName,rapid).apply(block).build()
    }

    private fun failListener(eventName: MessageType.Event,block: FailListenerBuilder.()-> Unit) {
        faillisteners.add(FailListenerBuilder(eventName,rapid).apply(block).build())
    }

    fun start() {
        eventListener.start()
        løser.forEach {
            start()
        }
        faillisteners.forEach { it.start() }
        dataListensers.forEach { it.start()}

    }
}

@DSLTopology
class EventListenerBuilder(private val eventName: MessageType.Event,
                           private val rapid: RapidsConnection,
                           val løser: MutableList<BehovRiver> = mutableListOf(),
                           val dataListensers: MutableList<DataRiver> = mutableListOf(),
                           val faillisteners: MutableList<FailRiver> = mutableListOf()
                            ) {
    @DSLTopology
    lateinit var implementation: IEventListener
    private lateinit var accept: River.PacketValidation

    @DSLTopology()
    fun accepts(jsonMessage: (JsonMessage) -> Unit)  {
        accept = River.PacketValidation {  }.apply { jsonMessage }
    }
    @DSLTopology
    fun løser(behov: MessageType.Behov, block: LøserBuilder.() -> Unit) {
        løser.add(LøserBuilder(behov,eventName, rapid).apply { block }.build())
    }

    @DSLTopology
    fun dataListener(block: DataListenerBuilder.() -> Unit) {
        dataListensers.add(DataListenerBuilder(eventName, rapid).apply { block }.build())
    }

    @DSLTopology
    fun failListener(block: FailListenerBuilder.()-> Unit) {
        faillisteners.add(FailListenerBuilder(eventName,rapid).apply(block).build())
    }

    internal fun build() : EventRiver {
        val validation = if (::accept.isInitialized) accept else implementation.accept() + River.PacketValidation{it.demandValue(
            Key.EVENT_NAME,eventName)}
        return EventRiver(rapid,implementation, validation)
    }

}
@DSLTopology
class FailListenerBuilder(private val event: MessageType.Event, private val rapid: RapidsConnection) {
    @DSLTopology
    lateinit var implementation: IFailListener

    private lateinit var accept: River.PacketValidation

    @DSLTopology
    fun accepts(jsonMessage: (JsonMessage) -> Unit)  {
        accept = River.PacketValidation {
             jsonMessage.invoke(it)
        }
    }

    internal fun build() : FailRiver {
        val validation = if (::accept.isInitialized) accept else implementation.accept() + River.PacketValidation{it.demandValue(
            Key.EVENT_NAME,event)}
        return FailRiver(rapid,implementation, validation)
    }

}
@DSLTopology
 class LøserBuilder(private val behov: MessageType.Behov,private val event: MessageType.Event, private val rapid: RapidsConnection) {

     @DSLTopology
    lateinit var implementation: IBehovListener
    private lateinit var accept: River.PacketValidation


    @DSLTopology
    fun accepts(jsonMessage: (JsonMessage) -> Unit)  {
        accept = River.PacketValidation {  }.apply { jsonMessage }
    }

    internal fun build() : BehovRiver {
        val validation = (if (::accept.isInitialized) accept else implementation.accept()) +
                {
                    it.demandValue(Key.EVENT_NAME,event)
                    it.demandValue(Key.BEHOV,behov)
                }
        return BehovRiver(rapid,implementation, validation)
    }

}


@DSLTopology
class DataListenerBuilder(private val event: MessageType.Event, private val rapid: RapidsConnection) {

    lateinit var implementation: IDataListener
    private lateinit var accept: River.PacketValidation


    @DSLTopology
    fun accepts(jsonMessage: (JsonMessage) -> Unit)  {
        accept = River.PacketValidation {  }.apply { jsonMessage }
    }

    @DSLTopology
    fun accepts(vararg datafelter: IDataFelt = arrayOf<IDataFelt>(), jsonMessage: (JsonMessage) -> Unit )  {
        accept = River.PacketValidation { it.interestedIn(*datafelter) }.apply { jsonMessage }
    }

    internal fun build() : DataRiver {
        val validation = (if (::accept.isInitialized) accept else implementation.accept()) +
                {
                    it.demandValue(Key.EVENT_NAME,event)
                }
        return DataRiver(rapid,implementation, validation)
    }

}



@DslMarker
annotation class DSLTopology

@DSLTopology
fun topology(rapid: RapidsConnection, block: TopologyBuilder.() -> Unit) : TopologyBuilder = TopologyBuilder(rapid).apply(block)

