package no.nav.jantra.river.configuration.dsl

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.jantra.river.IBehovListener
import no.nav.jantra.river.IDataFelt
import no.nav.jantra.river.IDataListener
import no.nav.jantra.river.IEventListener
import no.nav.jantra.river.IFailListener
import no.nav.jantra.river.Key
import no.nav.jantra.river.MessageType
import no.nav.jantra.river.ValidatedMessage
import no.nav.jantra.river.bridge.BehovRiver
import no.nav.jantra.river.bridge.DataRiver
import no.nav.jantra.river.bridge.EventRiver
import no.nav.jantra.river.bridge.FailRiver
import no.nav.jantra.river.demandValue
import no.nav.jantra.river.interestedIn
import no.nav.jantra.river.plus
import no.nav.jantra.river.redis.RedisStore
import java.lang.RuntimeException

@DSLTopology
class TopologyBuilder(private val rapid: RapidsConnection) {

    private lateinit var compositionBuilder : CompositionBuilder
    private lateinit var  sagaBuilder: SagaBuilder
    @DSLTopology
    fun composition(name: String = "",block: CompositionBuilder.() -> Unit) {
        compositionBuilder = CompositionBuilder(rapid).apply(block)
    }

    @DSLTopology
    fun saga(name: String = "",redisStore: RedisStore, block: SagaBuilder.() -> Unit) {
        sagaBuilder = SagaBuilder(rapid,redisStore).apply(block)
    }
    fun start() {
        if (::compositionBuilder.isInitialized) compositionBuilder.start()
        if (::sagaBuilder.isInitialized) sagaBuilder.start()
    }
}

@DSLTopology
class CompositionBuilder(private val rapid: RapidsConnection,
                         val løser: MutableList<BehovRiver> = mutableListOf(),
                         val dataListensers: MutableList<DataRiver> = mutableListOf(),
                         val faillisteners: MutableList<FailRiver> = mutableListOf()
) {
    private lateinit var eventListener:EventRiver
    private lateinit var event:MessageType.Event


    @DSLTopology
    fun eventListener(eventName: MessageType.Event ,block: EventListenerBuilder.()-> Unit) {
        event = eventName
        eventListener = EventListenerBuilder(eventName,rapid).apply(block).build()
    }
    @DSLTopology
    fun løser(behov: MessageType.Behov,block: LøserBuilder.() -> Unit) {
        if (!::event.isInitialized) throw RuntimeException("Eventlistener should be declared first")
        løser.add(LøserBuilder(behov, event, rapid).apply(block).build())
    }
    @DSLTopology
     fun dataListener(block: DataListenerBuilder.() -> Unit) {
         if (!::event.isInitialized) throw RuntimeException("Eventlistener should be declared first")
         dataListensers.add(DataListenerBuilder(event, rapid).apply ( block ).build())
    }
    @DSLTopology
    fun failListener(block: FailListenerBuilder.()-> Unit) {
        if (!::event.isInitialized) throw RuntimeException("Eventlistener should be declared first")
        faillisteners.add(FailListenerBuilder(event,rapid).apply(block).build())
    }

    fun start() {
        eventListener.start()
        løser.forEach {
            it.start()
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
        accept = River.PacketValidation {
            jsonMessage.invoke(it)
        }
    }
    @DSLTopology
    fun løser(behov: MessageType.Behov, block: LøserBuilder.() -> Unit) {
        løser.add(LøserBuilder(behov,eventName, rapid).apply (block ).build())
    }

    @DSLTopology
    fun dataListener(block: DataListenerBuilder.() -> Unit) {
        dataListensers.add(DataListenerBuilder(eventName, rapid).apply ( block ).build())
    }

    @DSLTopology
    fun failListener(block: FailListenerBuilder.()-> Unit) {
        faillisteners.add(FailListenerBuilder(eventName,rapid).apply(block).build())
    }

    internal fun build() : EventRiver {
        var inlineValidator = if (!::accept.isInitialized  &&  implementation is ValidatedMessage) (implementation as ValidatedMessage).accept() else River.PacketValidation{}

        val validation = (if (::accept.isInitialized) accept else inlineValidator) +  River.PacketValidation{it.demandValue(
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
        var inlineValidator = if (!::accept.isInitialized  &&  implementation is ValidatedMessage) (implementation as ValidatedMessage).accept() else River.PacketValidation{}
        val validation = (if (::accept.isInitialized) accept else inlineValidator) + River.PacketValidation{it.demandValue(
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
        accept = River.PacketValidation {
            jsonMessage.invoke(it)
        }
    }

    internal fun build() : BehovRiver {
        var inlineValidator = if (!::accept.isInitialized  &&  implementation is ValidatedMessage) (implementation as ValidatedMessage).accept() else River.PacketValidation{}
        val validation = (if (::accept.isInitialized) accept else inlineValidator) +
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
        var inlineValidator = if (!::accept.isInitialized  &&  implementation is ValidatedMessage) (implementation as ValidatedMessage).accept() else River.PacketValidation{}
        val validation = (if (::accept.isInitialized) accept else inlineValidator) +
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
@DSLTopology
fun composition(name: String = "",rapid: RapidsConnection,block: CompositionBuilder.() -> Unit)  = CompositionBuilder(rapid).apply(block)


