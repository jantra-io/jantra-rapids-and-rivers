package no.nav.jantra.river.configuration.dsl

import jdk.jfr.Experimental
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
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
import no.nav.jantra.river.plus


@Experimental
class ListenerDSLBuilder(private val rapid:RapidsConnection,
    val løser: MutableList<BehovRiver> = mutableListOf(),) {
    @DSLListener
    lateinit var event: MessageType.Event
    private lateinit var eventRiver: EventRiver
    private lateinit var dataRiver: DataRiver
    private lateinit var failRiver: FailRiver
    @DSLListener
    fun eventListener(block: EventListenerDSLBuilder.()->Unit)  {
        eventRiver = EventListenerDSLBuilder(event,rapid).apply(block).build()
    }

    fun dataListener(block: DataListenerDSLBuilder.()->Unit) {
        dataRiver = DataListenerDSLBuilder(event,rapid).apply(block).build()
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

@Experimental
class EventListenerDSLBuilder(private val event: MessageType.Event, private val rapid: RapidsConnection) {
    @DSLListener
    lateinit var implementation: IEventListener
    private lateinit var accept: River.PacketValidation
    @DSLListener
    fun accepts(packetValidation: River.PacketValidation)  {
        accept = packetValidation
    }

    internal fun build() : EventRiver {
        var inlineValidator = if (!::accept.isInitialized  &&  implementation is ValidatedMessage) (implementation as ValidatedMessage).accept() else River.PacketValidation{}
        val validation = if (::accept.isInitialized) accept else inlineValidator + River.PacketValidation{it.demandValue(
            Key.EVENT_NAME,event)}
        return EventRiver(rapid,implementation, validation)
    }

}

@Experimental
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

@Experimental
class DataListenerDSLBuilder(private val event: MessageType.Event, private val rapid: RapidsConnection) {
    private lateinit var accept: River.PacketValidation
    @DSLListener
    lateinit var implementation: IDataListener


    @DSLListener
    fun accepts(jsonMessage: (JsonMessage) -> Unit)  {
        accept = River.PacketValidation {  }.apply { jsonMessage }
    }

    fun build() : DataRiver {
        if (!this::accept.isInitialized) accept = River.PacketValidation {
            it.demandValue(Key.EVENT_NAME.str,event.value)
        }
        return DataRiver(rapid,implementation,accept)
    }

}

@Experimental
@DslMarker
annotation class DSLListener

@DSLListener
fun listener( rapid:RapidsConnection, block: ListenerDSLBuilder.() -> Unit) : ListenerDSLBuilder = ListenerDSLBuilder(rapid).apply(block)

